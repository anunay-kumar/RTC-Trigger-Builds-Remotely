package com.devopscode.net;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.eclipse.core.runtime.IProgressMonitor;
import com.ibm.team.build.client.ITeamBuildClient;
import com.ibm.team.build.client.ITeamBuildRequestClient;
import com.ibm.team.build.common.BuildItemFactory;
import com.ibm.team.build.common.model.IBuildDefinition;
import com.ibm.team.build.common.model.IBuildProperty;
import com.ibm.team.build.common.model.IBuildRequest;
import com.ibm.team.build.common.model.IBuildRequestHandle;
import com.ibm.team.build.common.model.IBuildResult;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.internal.ItemManager;
import com.ibm.team.repository.client.internal.TeamRepository;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.IItemHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;
import com.ibm.team.scm.common.internal.IScmQueryService;
import com.ibm.team.scm.common.internal.dto.ItemQueryResult;




public class BuildAuto {

	private String RTC_HOST_SERVER;
	private String USERNAME;
	private String PASSWORD;
	private IProgressMonitor monitor;
	private static String BUILD_DEFINITION = null;
	private static String BUILD_WORKSPACE = null;
	private static String BUILD_PROPERTIES = null;
	private static String IS_PERSONAL = null;

	public BuildAuto(String rtcAddress, String username, String password) {
		TeamPlatform.startup();
		this.RTC_HOST_SERVER = rtcAddress;
		this.USERNAME = username;
		this.PASSWORD = password;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		boolean invalid = true;
		if (args != null) {
			if (args.length == 7) {
				invalid = false;
			}
		}

		if (invalid) {
			System.out.println("Incorrect usage. Please use the following formats: parameter count is not 7");
			System.out.println("USAGE: " + "<rtc_server_address> <username> <password> <build_definition UUID> <build_workspace> <comma-seperated key-value pairs> <is_personal>");
			System.out.println("Example: " + "https://nsjazz.devopscode.net:8043/jazz user@devopscode.com password \"Demo Build\" \"ANUNAY Personal Branch\" \"NAME=Anunay,IP=1.2.3.4,USER=anunay,PWD=P@88w0rd#\" true");
			System.exit(-1);
		} else {
			try {
				
				for(int i=0;i<args.length;i++) {
					if(i==2) {
						System.out.println("Param" + i + ": ****");
					} else {
						System.out.println("Param" + i + ": " + args[i]);
					}
					
				}
				
				BuildAuto bldAuto = new BuildAuto(args[0], args[1], args[2]);
				ITeamRepository repository = bldAuto.login();
				if (repository != null) {
					System.out.println("[Info]: Logged in successfully!");
				} else {
					throw new Exception("[Error]: Unable to login to rtc server!");
				}
				//Set the build definition UUID
				BUILD_DEFINITION=args[3];
				BUILD_WORKSPACE=args[4];
				BUILD_PROPERTIES=args[5];
				IS_PERSONAL=args[6];
				bldAuto.triggerBuild(repository);

			} catch (TeamRepositoryException e) {
				System.out.println("[Error] Check if user is allowed to login or if the credentials are correct");
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}
	}

	public ITeamRepository login() throws TeamRepositoryException {
		ITeamRepository repository = TeamPlatform.getTeamRepositoryService().getTeamRepository(RTC_HOST_SERVER);
		repository.registerLoginHandler(new ITeamRepository.ILoginHandler() {
			public ILoginInfo challenge(ITeamRepository repository) {
				return new ILoginInfo() {
					public String getUserId() {
						return USERNAME;
					}

					public String getPassword() {
						return PASSWORD;
					}
				};
			}
		});
		repository.login(this.monitor);
		return repository;
	}
	
	public String getWorkspaceUUIDFromName(ITeamRepository repository, String workspaceName) throws TeamRepositoryException {
		IWorkspaceSearchCriteria search = IWorkspaceSearchCriteria.FACTORY.newInstance();
		search.setExactName(workspaceName);
		IScmQueryService scmService = (IScmQueryService) ((TeamRepository) repository).getServiceInterface(IScmQueryService.class);
		ItemQueryResult foundWorkspace = scmService.findWorkspaces(search, 10, null);
		if (foundWorkspace != null) {
			IWorkspaceHandle workspaceHandle = (IWorkspaceHandle) foundWorkspace.getItemHandles().get(0);
			System.out.println("Workspace UUID: " + workspaceHandle.getItemId());
			return workspaceHandle.getItemId().getUuidValue();
		}
		 return null;
	}
	
	/*
	public UUID getBuildDefUUIDFromName(ITeamBuildClient buildClient, String buildDefName) throws TeamRepositoryException {
		IBuildEngineQueryModel buildDefnitionQueryModel = IBuildEngineQueryModel.ROOT;
		IItemQuery query = IItemQuery.FACTORY.newInstance(buildDefnitionQueryModel);
		query.filter(buildDefnitionQueryModel.itemId()._eq(query.newUUIDArg()));
		
		IItemQueryPage queryPage = buildClient.queryItems(query, new Object[] { buildDefinition.getItemId() }, IQueryService.ITEM_QUERY_MAX_PAGE_SIZE, monitor);
		
		if (queryPage.getResultSize() == 0) {
			IWorkspaceHandle buildDefHandle = (IWorkspaceHandle) foundBuildDef.getItemHandles().get(0);
			return buildDefHandle.getItemId();
		}
		 return null;
	}*/

	public ITeamRepository login(String RepoAddress) throws TeamRepositoryException {
		ITeamRepository repository = TeamPlatform.getTeamRepositoryService().getTeamRepository(RepoAddress);
		repository.registerLoginHandler(new ITeamRepository.ILoginHandler() {
			public ILoginInfo challenge(ITeamRepository repository) {
				return new ILoginInfo() {
					public String getUserId() {
						return USERNAME;
					}

					public String getPassword() {
						return PASSWORD;
					}
				};
			}
		});
		repository.login(this.monitor);;
		return repository;
	}
	
	public String convertDate(long epochTime) {
		Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        return formatted;
	}
	
	
	public void triggerBuild(ITeamRepository repository) throws IllegalArgumentException, TeamRepositoryException, InterruptedException {
		try {
			// See if we get back a valid repository
			System.out.println("[Info] Repo UUID: " + repository.getId());
			ITeamBuildClient buildClient = (ITeamBuildClient) repository.getClientLibrary(ITeamBuildClient.class);
			IBuildDefinition buildDefinition = buildClient.getBuildDefinition(BUILD_DEFINITION, monitor);
			
			
			if (buildDefinition == null) {
	            System.out.println("[Error] build definition with id \"" + BUILD_DEFINITION + "\" does not exist.");
	            System.exit(1);
	        }
			
					
			//Adding property
			String MOD_BUILD_PROPERTIES = BUILD_PROPERTIES + ",team.scm.workspaceUUID=" + getWorkspaceUUIDFromName(repository, BUILD_WORKSPACE);
			
			String[] propArray = MOD_BUILD_PROPERTIES.split(",");
			IBuildProperty[] buildProps = new IBuildProperty[propArray.length];
			for (int i=0;i<propArray.length;i++) {
				System.out.println("Build Property: " + propArray[i]);
				String buildPropName = propArray[i].split("=")[0];
	    		String buildPropValue = propArray[i].split("=")[1];
	    		IBuildProperty buildProp = BuildItemFactory.createBuildProperty();
	    		if (buildPropName.contains("team.scm.workspaceUUID")) {
	    			buildProp.setKind("com.ibm.team.scm.property.workspace");
		    		buildProp.setName(buildPropName);
		    		buildProp.setValue(buildPropValue);
		    		buildProps[i]=buildProp;
	    		} else {
		    		buildProp.setKind(IBuildProperty.PROPERTY_KIND_STRING);
		    		buildProp.setName(buildPropName);
		    		buildProp.setValue(buildPropValue);
		    		buildProps[i]=buildProp;
	    		}
			}
			
			//Set build type
			boolean isPersonal = false;
			if (IS_PERSONAL.toLowerCase().contains("true")) {
				isPersonal = true;
			}

	        ITeamBuildRequestClient requestClient = (ITeamBuildRequestClient) repository.getClientLibrary(ITeamBuildRequestClient.class);
	        IBuildRequest buildRequest = requestClient.requestBuild(buildDefinition, buildProps, null, isPersonal, isPersonal, monitor);
	        
	        /*
	        for (int i=0; i<buildDefinition.getProperties().size(); i++) {
	        	IBuildProperty ibp = (IBuildProperty) buildDefinition.getProperties().get(i);
	        	System.out.println("--\n" + ibp.getKind() + "\n" + ibp.getName() + "\n" + ibp.getValue() + "\n" + ibp.getClass() + "--");
	        	
	        }*/
	        
	        /*
	        for (int x=0;x<buildDefinition.getConfigurationElements().size();x++) {
				IBuildConfigurationElement buildConfElem = (IBuildConfigurationElement) buildDefinition.getConfigurationElements().get(x);
				System.out.println("|--" + buildConfElem.getElementId() + "=" + buildConfElem.getName());
				if (buildConfElem.getElementId().equalsIgnoreCase("com.ibm.team.build.jazzscm")) {
					List <IConfigurationProperty> buildConfigProperty =  buildConfElem.getConfigurationProperties();
					for (int i=0;i<buildConfigProperty.size();i++ ){
						String name = buildConfigProperty.get(i).getName();
						String value = buildConfigProperty.get(i).getValue();
						System.out.println(" |++++name:" +  name +  " value: " +  value);
					}
				}
	        }*/
	        
	        IBuildRequestHandle requestHandle = (IBuildRequestHandle) IBuildRequest.ITEM_TYPE.createItemHandle(UUID.valueOf(buildRequest.getItemId().getUuidValue()), null);
	        IBuildRequest request = (IBuildRequest) repository.itemManager().fetchCompleteItem((IItemHandle) requestHandle,ItemManager.REFRESH, null);
	        IBuildResult result = (IBuildResult) repository.itemManager().fetchCompleteItem((IItemHandle) request.getBuildResult(),ItemManager.REFRESH, null);
	        
	        IContributorHandle buildOwnerHandle = result.getModifiedBy();
	        IContributor buildOwner = (IContributor) repository.itemManager().fetchCompleteItem(buildOwnerHandle, IItemManager.DEFAULT, monitor);
	        
	        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++");
	        System.out.println("Initial Build State: " + result.getState());
	        System.out.println("Build Label: " + result.getLabel());
	        System.out.println("Build Started By: " + buildOwner.getEmailAddress() + " (" + buildOwner.getName() + ")");
	        System.out.println("Personal Build: " + result.isPersonalBuild());
	        System.out.println("Started At: " + request.getCreated());
	        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++\n");
	        
	        
	        long start = System.currentTimeMillis();
	        long end = start + 180000000; //5 Hours timeout 
	        while(!result.getState().name().equalsIgnoreCase(("COMPLETED")))
	        {
	        	result = (IBuildResult) repository.itemManager().fetchCompleteItem((IItemHandle) request.getBuildResult(),
		                ItemManager.REFRESH, null);
	            System.out.println("Waiting for build to complete..is it complete: " + result.getState());
	            if (result.getState().name().equalsIgnoreCase(("COMPLETED"))) {
	            	System.out.println("Total time taken (Seconds): " +  TimeUnit.MILLISECONDS.toSeconds(result.getBuildTimeTaken()));
	            }
	            if(System.currentTimeMillis() > end) {
	            	System.out.println("[Error] Timed out waiting for build to complete");
	            	System.exit(1);
	            }
	            Thread.sleep(3000);
	        }
	        
	        requestHandle = (IBuildRequestHandle) IBuildRequest.ITEM_TYPE.createItemHandle(UUID.valueOf(buildRequest.getItemId().getUuidValue()), null);
	        request = (IBuildRequest) repository.itemManager().fetchCompleteItem((IItemHandle) requestHandle,ItemManager.REFRESH, null);
	        result = (IBuildResult) repository.itemManager().fetchCompleteItem((IItemHandle) request.getBuildResult(),ItemManager.REFRESH, null);
	        
	        buildOwnerHandle = result.getModifiedBy();
	        buildOwner = (IContributor) repository.itemManager().fetchCompleteItem(buildOwnerHandle, IItemManager.DEFAULT, monitor);
	        
	        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++");
	        System.out.println("Final Build State: " + result.getState());
	        System.out.println("Build Label: " + result.getLabel());
	        System.out.println("Build Started By: " + buildOwner.getEmailAddress() + " (" + buildOwner.getName() + ")");
	        System.out.println("Personal Build: " + result.isPersonalBuild());
	        System.out.println("Started At: " + request.getCreated());
	        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++\n");
	        
		} catch (Exception e) {
			System.out.println("Exception Message: " + e.getMessage());
		}
	}
}
