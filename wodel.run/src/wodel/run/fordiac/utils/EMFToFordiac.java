package wodel.run.fordiac.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.osgi.framework.Bundle;

import wodel.run.popup.actions.RunFordiacWodel;

public class EMFToFordiac {
	private static final String FILE_EXTENSION = "opsem"; //$NON-NLS-1$
	private static Bundle bundle = Platform.getBundle("org.eclipse.fordiac.ide.test.fb.interpreter"); //$NON-NLS-1$
	private static FordiacProjectLoader loader;
	private static IResource[] resources;
	
	public static IResource emfToFordiac(Resource model) {
		RunFordiacWodel.init4diacEPackages(model.getResourceSet());
		
		IResource diac = RunFordiacWodel.toFordiacResource(model);
		return diac;
	}
}
