package wodel.run.fordiac.utils;

import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


public class FordiacToEMF {
	private static final String FILE_EXTENSION = "opsem"; //$NON-NLS-1$
	private static Bundle bundle = Platform.getBundle("org.eclipse.fordiac.ide.test.fb.interpreter"); //$NON-NLS-1$
	private static FordiacProjectLoader loader;
	private static IResource[] resources;
	
	public static IResource[] loadProject() throws CoreException {
		try {
			loader = new FordiacProjectLoader(new Path(Platform.getLocation().toFile().getPath() + "/MutationTestingApp"));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final IProject project = loader.getEclipseProject();
		final IFolder folder = project.getFolder("network_traces"); //$NON-NLS-1$
		if (!folder.exists()) {
			throw new UnsupportedOperationException("could not find traces in project"); //$NON-NLS-1$
		}
		resources = folder.members(false);
		return resources;
	}
}
