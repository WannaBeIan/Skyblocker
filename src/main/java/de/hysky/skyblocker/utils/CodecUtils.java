package de.hysky.skyblocker.utils;

import com.mojang.serialization.MapCodec;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public final class CodecUtils {

	private CodecUtils() {
		throw new IllegalStateException("Uhhhh no? like just no. What are you trying to do? D- Do you think this will be useful to instantiate this? Like it's private, so you went through the effort of putting an accessor actually i'm not sure you can accessor a constructor. can you? so if not did you really put an access widener for that? like really? honestly this is just sad. Plus there aren't even any method in here that requires an instance. There's only static methods. like bruh. you know what i'm done typing shit for you to read, bye i'm leaving *voice lowers as I leave* I swear those modders think they can access all they want sheesh *comes back instantly* AND I SWEAR IF YOU INJECT SO THIS ERROR CANNOT BE THROWN I WILL SEND YOU TO HELL'S FREEZER");
	}

	public static MapCodec<OptionalInt> optionalInt(MapCodec<Optional<Integer>> codec) {
		return codec.xmap(opt -> opt.map(OptionalInt::of).orElseGet(OptionalInt::empty), optInt -> optInt.isPresent() ? Optional.of(optInt.getAsInt()) : Optional.empty());
	}

	public static MapCodec<OptionalDouble> optionalDouble(MapCodec<Optional<Double>> codec) {
		return codec.xmap(opt -> opt.map(OptionalDouble::of).orElseGet(OptionalDouble::empty), optDouble -> optDouble.isPresent() ? Optional.of(optDouble.getAsDouble()) : Optional.empty());
	}
}