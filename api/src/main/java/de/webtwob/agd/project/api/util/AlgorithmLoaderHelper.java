package de.webtwob.agd.project.api.util;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import de.webtwob.agd.project.api.interfaces.IAlgorithm;

public class AlgorithmLoaderHelper {
	private static final List<IAlgorithm> loader = ServiceLoader.load(IAlgorithm.class).stream()
			.map(ServiceLoader.Provider::get).collect(Collectors.toList());

	public static List<IAlgorithm> getAlgorithms(){
		return loader.stream().collect(Collectors.toList());
	}
	
}
