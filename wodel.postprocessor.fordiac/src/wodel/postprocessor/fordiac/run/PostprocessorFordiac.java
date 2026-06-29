package wodel.postprocessor.fordiac.run;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.fordiac.ide.fb.interpreter.OpSem.OperationalSemanticsPackage;

import wodel.postprocessor.run.IPostprocessor;
import wodel.run.fordiac.utils.EMFToFordiac;
import wodel.run.popup.actions.RunFordiacWodel;

/**
 * @author Pablo Gomez-Abajo - Wodel postprocessing extension to convert mutants to 4diac format
 * 
 */

public class PostprocessorFordiac implements IPostprocessor {
	@Override
	public String getName() {

		return "Static: generates mutants in 4-diac file format";
		// TODO Auto-generated method stub

	}
	
	@Override
	public String getURI() {
		return "";
	}

	@Override
	public boolean doProcess(String metamodelpath, List<String> metamodel, Resource model, String filename) {
		// TODO Auto-generated method stub
		// EMFJSON
		try {
			ResourceSet resourceSet = model.getResourceSet();
			RunFordiacWodel.init4diacEPackages(resourceSet);
			
			if (!model.isLoaded()) {
				// 1) Ensure resource factories are registered (important in nested runtimes)
			    resourceSet.getResourceFactoryRegistry()
			        .getExtensionToFactoryMap()
			        .put("model", new XMIResourceFactoryImpl());
			    resourceSet.getResourceFactoryRegistry()
			        .getExtensionToFactoryMap()
			        .put("xmi", new XMIResourceFactoryImpl());
			    resourceSet.getResourceFactoryRegistry()
			        .getExtensionToFactoryMap()
			        .put("*", new XMIResourceFactoryImpl());

			    // 2) ExtendedMetaData: prevents issues with feature maps / namespaces
			    ExtendedMetaData emd = new BasicExtendedMetaData(resourceSet.getPackageRegistry());

			    // 3) Load options
			    final Map<Object, Object> options = resourceSet.getLoadOptions();
			    options.put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl());
			    options.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.FALSE);
			    options.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<Object, Object>());

			    // If you want fast/lenient:
			    options.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
			    options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);

			    // Unknown features: don't fail if model contains stuff outside registry
			    options.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);

			    // ✅ same fix as saving: provide ExtendedMetaData for namespace handling
			    options.put(XMLResource.OPTION_EXTENDED_META_DATA, emd);

			    // HREF handling:
			    // DISCARD => drop unresolved refs (can produce incomplete graphs)
			    // RECORD  => keep diagnostics without throwing away the structure as aggressively
			    options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF,
			            XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			    EPackage op = resourceSet.getPackageRegistry().getEPackage(OperationalSemanticsPackage.eNS_URI);
			    System.out.println("OP pkg: " + op);
			    if (op != null && op.getEClassifier("EventManager") instanceof org.eclipse.emf.ecore.EClass) {
			        org.eclipse.emf.ecore.EClass em = (org.eclipse.emf.ecore.EClass) op.getEClassifier("EventManager");
			        System.out.println("EventManager.transactions = " + em.getEStructuralFeature("transactions"));
			    }
			    
		        model.load(options);
			}
			
			IResource diac = EMFToFordiac.emfToFordiac(model);
			
			//List<EPackage> packages = ModelManager.loadMetaModel(metamodelpath);
			
			if (model.getContents().isEmpty()) {
				return true;
			}
//			EPackage pck = model.getContents().get(0).eClass().getEPackage();
//			List<EPackage> packages = new ArrayList<EPackage>();
//			packages.add(pck);
//			
//			if (!model.isLoaded()) {
//				model = ModelManager.loadModel(packages, model.getURI().toString());
//			}
//			

		    // Ensure the extension is handled (use your actual extension: "opsem" / "model" / etc.)
		    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
		      .put("opsem", new XMIResourceFactoryImpl());

		    // 2) Create an EMF Resource with a platform:/resource URI (nice for traceability)
		    URI emfUri = URI.createPlatformResourceURI(model.getURI().toString().replace(".model", ".opsem"), true);
		    Resource emfRes = resourceSet.createResource(emfUri);
		    emfRes.getContents().clear();
		    emfRes.getContents().add(model.getContents().get(0));

		    // Optional: resolve proxies before save (only if you expect them resolvable)
		    // EcoreUtil.resolveAll(rs);

		    // 3) Save EMF -> bytes
		    Map<Object, Object> saveOptions = new HashMap<>();
		    saveOptions.put(XMLResource.OPTION_ENCODING, "UTF-8");
		    saveOptions.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		    saveOptions.put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.TRUE);
		    saveOptions.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);

		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    emfRes.save(baos, saveOptions);

		    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			if (root == null) {
				throw new IllegalArgumentException("Only Resource can be transformed to 4-diac model.");
			}
			
		    // 4) Write to workspace IFile
		    IFolder mutants = root.getProject("MutationTestingApp").getFolder("mutants");
		    if (!mutants.exists()) {
		    	mutants.create(true, true, null);
		    }
		    File f = new File(filename);
		    if (!f.exists() || f.isDirectory() || !filename.endsWith(".model")) {
		    	return true;
		    }
		    String finalFileName = filename.replace("\\", "/");
		    String[] folders = finalFileName.split("/");
		    int index = 0;
		    for (int i = 0; i < folders.length - 1; i++) {
		    	if (folders[i].equals("MutationTestingApp")) {
		    		index = i;
		    		break;
		    	}
		    }
		    IFolder target = mutants;
		    for (int i = index; i < folders.length - 1; i++) {
		    	target = target.getFolder(folders[i]);
		    	if (!target.exists()) {
		    		target.create(true, true, null);
		    	}
		    }
		    finalFileName = finalFileName.substring(finalFileName.lastIndexOf("/")).replace(".model", ".opsem");
		    IFile targetOpsemFile = target.getFile(finalFileName);
		    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		    if (!targetOpsemFile.exists()) {
		    	targetOpsemFile.create(bais, true, null);
		    }
		    else {
		    	targetOpsemFile.setContents(bais, true, true, null);
		    }
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (IOException e0) {
			// TODO Auto-generated catch block
			e0.printStackTrace();
			return false;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		// TODO Auto-generated method stub
		
	}
}
