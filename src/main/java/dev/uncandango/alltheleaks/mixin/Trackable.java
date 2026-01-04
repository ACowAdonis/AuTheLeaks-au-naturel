package dev.uncandango.alltheleaks.mixin;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public interface Trackable {
	IdentityHashMap<Class<?>, ObjectOpenCustomHashSet<WeakReference<Trackable>>> TRACKABLE_MAP = new IdentityHashMap<>();
	Hash.Strategy<WeakReference<Trackable>> WEAK_REFERENCE_STRATEGY = new Hash.Strategy<>() {
		@Override
		public int hashCode(WeakReference<Trackable> o) {
			Trackable t = o.get();
			// Use identity hash of referent if alive, otherwise identity hash of the WeakReference itself
			// This prevents all dead references from clustering at hash code 0
			return t != null ? System.identityHashCode(t) : System.identityHashCode(o);
		}

		@Override
		public boolean equals(WeakReference<Trackable> o, WeakReference<Trackable> k1) {
			return k1 != null && k1.get() == o.get();
		}
	};
	ReentrantLock LOCK = new ReentrantLock();

	default WeakReference<Trackable> wrap(){
		return new WeakReference<>(this);
	}

	default void startTracking(){
		LOCK.lock();
		TRACKABLE_MAP.computeIfAbsent(atl$getBaseClass(), (key) -> Trackable.createWeakRefBasedSet()).add(wrap());
		LOCK.unlock();
	}

	static ObjectOpenCustomHashSet<WeakReference<Trackable>> createWeakRefBasedSet(){
		return new ObjectOpenCustomHashSet<>(WEAK_REFERENCE_STRATEGY) {
			@Override
			public boolean trim() {
				this.removeIf(reference -> reference.get() == null);
				return super.trim();
			}
		};
	}

	static void clearNullReferences(){
		LOCK.lock();
		try {
			// Remove null references and compact sets
			TRACKABLE_MAP.replaceAll((clazz, set) -> {
				int sizeBefore = set.size();
				// Remove dead weak references
				set.removeIf(ref -> ref.get() == null);
				int sizeAfter = set.size();
				// If we removed more than 75% of entries, rebuild with right-sized array
				// This ensures the backing array shrinks to match the actual usage
				if (sizeAfter > 0 && sizeBefore > 16 && sizeAfter < sizeBefore / 4) {
					var newSet = createWeakRefBasedSet();
					newSet.addAll(set);
					newSet.trim();
					return newSet;
				}
				set.trim();
				return set;
			});
			// Remove empty entries from the map entirely
			TRACKABLE_MAP.entrySet().removeIf(entry -> entry.getValue().isEmpty());
		} finally {
			LOCK.unlock();
		}
	}

	static void startTracking(Object o){
		if (o instanceof Trackable trackable) {
			trackable.startTracking();
		}
	}

	static Map<Class<?>, Map<Class<?>, Long>> getSummary(){
		Map<Class<?>, Map<Class<?>, Long>> resultMap = new HashMap<>();
		LOCK.lock();
		Trackable.TRACKABLE_MAP.forEach((key, value) -> {
			Map<Class<?>, Long> innerMap = value.stream()
				.map(Reference::get)
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Object::getClass, Collectors.counting()));
			resultMap.put(key, innerMap);
		});
		LOCK.unlock();
		return resultMap;
	}

	Class<?> atl$getBaseClass();
}
