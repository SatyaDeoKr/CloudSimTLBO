/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.lists.VmList;
/**
 * NetDatacentreBroker represents a broker acting on behalf of Datacenter
 * provider. It hides VM management, as vm creation, submission of cloudlets to
 * this VMs and destruction of VMs. NOTE- It is an example only. It work on
 * behalf of a provider not for users. One has to implement interaction with
 * user broker to this broker.
 * 
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 3.0
 */
public class Leader_NetdatacenterBroker extends SimEntity {

	// TODO: remove unnecessary variables

	/** The vm list. */
	private List<? extends Vm> vmList;

	/** The vms created list. */
	private List<? extends Vm> vmsCreatedList;

	/** The cloudlet list. */
	private List<? extends NetworkCloudlet> cloudletList;

	private List<? extends AppCloudlet> appCloudletList;

	/** The Appcloudlet submitted list. */
	private final Map<Integer, Integer> appCloudletRecieved;

	private List<? extends Cloudlet> cloudletSubmittedList;

	/** The cloudlet received list. */
	private List<? extends Cloudlet> cloudletReceivedList;

	/** The cloudlets submitted. */
	private int cloudletsSubmitted;

	/** The vms requested. */
	private int vmsRequested;

	/** The vms acks. */
	private int vmsAcks;

	/** The vms destroyed. */
	private int vmsDestroyed;

	/** The datacenter ids list. */
	private List<Integer> datacenterIdsList;

	/** The datacenter requested ids list. */
	private List<Integer> datacenterRequestedIdsList;

	/** The vms to datacenters map. */
	private Map<Integer, Integer> vmsToDatacentersMap;

	/** The datacenter characteristics list. */
	private Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;

	public static LeaderNetworkDatacenter linkDC;
	
	private PSO psoScheduling;
	private IEAM ieamScheduling;

	private Leadero LeaderoScheduling;
	
	public boolean createvmflag = true;

	public static int cachedcloudlet = 0;
	
	public static double totalTransferCost = 0;
	
	public static double totalExeCost = 0;
	
	public static double totalCost = 0;
	
	private static int numberOfTasks = 10; //number of cloudlets
	
	public static int numberOfVms = 8; //number of VMs
	
	public static double[] executionCost; //execution cost of each vm
	
	public static double[][] transferCost; //transfer cost from one vm to another vm
	
	//public static double[] mips;
	
	public static double[] workflowExecutionMI;
	
	public double[][] workflowDataTransferMap;
	
	public int numHosts=4;
	
	//public static double[] executionCost = {1.26, 1.25, 1.24, 1.18, 1.12, 1.27, 1.25, 1.14};
	public static double[] exeCost = {1.21, 1.2, 1.24, 1.18, 1.12, 1.27, 1.25, 1.14};//{1,1,1,1,1,1,1,1};
	public static double[][] trCost = {{0, 0.17, 0.20, 0.20, 0.21, 0.21, 0.18, 0.18},
			                   {0.17, 0, 0.20, 0.20, 0.21, 0.21, 0.18, 0.18},
			                   {0.20, 0.20, 0, 0.17, 0.22, 0.22, 0.19, 0.19},
			                   {0.20, 0.20, 0.17, 0, 0.22, 0.22, 0.19, 0.19},
			                   {0.21, 0.21, 0.22, 0.22, 0, 0.17, 0.20, 0.20},
			                   {0.21, 0.21, 0.22, 0.22, 0.17, 0, 0.20, 0.20},
			                   {0.18, 0.18, 0.19, 0.19, 0.20, 0.20, 0, 0.17},
			                   {0.18, 0.18, 0.19, 0.19, 0.20, 0.20, 0.17, 0}
			                   //{0.18, 0.18, 0.19, 0.19, 0.20, 0.20, 0.17, 0.21, 0, 0.21},
			                   //{0.18, 0.18, 0.19, 0.19, 0.20, 0.20, 0.17, 0.21, 0.21, 0}
			                   };
	//public static double[] mips = {1, 1, 1, 1, 1, 1, 1, 1};
	
	
	
	public static double[] mips = {1.011, 1.004, 1.013, 1.0, 0.91, 1.043, 1.023, 0.998};
	/**
	 * Created a new DatacenterBroker object.
	 * 
	 * @param name
	 *            name to be associated with this entity (as required by
	 *            Sim_entity class from simjava package)
	 * 
	 * @throws Exception
	 *             the exception
	 * 
	 * @pre name != null
	 * @post $none
	 */
	public Leader_NetdatacenterBroker(String name) throws Exception {
		super(name);
		setVmList(new ArrayList<NetworkVm>());
		setVmsCreatedList(new ArrayList<NetworkVm>());
		setCloudletList(new ArrayList<NetworkCloudlet>());
		setAppCloudletList(new ArrayList<AppCloudlet>());
		setCloudletSubmittedList(new ArrayList<Cloudlet>());
		setCloudletReceivedList(new ArrayList<Cloudlet>());
		appCloudletRecieved = new HashMap<Integer, Integer>();

		cloudletsSubmitted = 0;
		setVmsRequested(0);
		setVmsAcks(0);
		setVmsDestroyed(0);

		setDatacenterIdsList(new LinkedList<Integer>());
		setDatacenterRequestedIdsList(new ArrayList<Integer>());
		setVmsToDatacentersMap(new HashMap<Integer, Integer>());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());

	}

	/**
	 * This method is used to send to the broker the list with virtual machines
	 * that must be created.
	 * 
	 * @param list
	 *            the list
	 * 
	 * @pre list !=null
	 * @post $none
	 */
	public void submitVmList(List<? extends Vm> list) {
		
		getVmList().addAll(list);
	}

	/**
	 * This method is used to send to the broker the list of cloudlets.
	 * 
	 * @param list
	 *            the list
	 * 
	 * @pre list !=null
	 * @post $none
	 */
	public void CloudletList(List<? extends NetworkCloudlet> list) {
		getCloudletList().addAll(list);
	}

	public void setLinkDC(LeaderNetworkDatacenter datacenter0) {
		linkDC = datacenter0;
	}

	/**
	 * Processes events available for this Broker.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * 
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	//used
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		// Resource characteristics request
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
			processResourceCharacteristicsRequest(ev);
			break;
		// Resource characteristics answer
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
			processResourceCharacteristics(ev);
			break;
		// VM Creation answer

		// A finished cloudlet returned
		case CloudSimTags.CLOUDLET_RETURN:
			processCloudletReturn(ev);
			break;
		// if the simulation finishes
		case CloudSimTags.END_OF_SIMULATION:
			shutdownEntity();
			break;
		case CloudSimTags.NextCycle:
			if (NetworkConstants.BASE) {
				createVmAndWorkflow(linkDC.getId());
			}

			break;
		// other unknown tags are processed by this method
		default:
			processOtherEvent(ev);
			break;
		}
	}

	/**
	 * Process the return of a request for the characteristics of a
	 * PowerDatacenter.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * 
	 * @pre ev != $null
	 * @post $none
	 */
	//used
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev
				.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(),
				characteristics);

		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList()
				.size()) {
			setDatacenterRequestedIdsList(new ArrayList<Integer>());
			createVmAndWorkflow(getDatacenterIdsList().get(0));
		}
	}

	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * 
	 * @pre ev != $null
	 * @post $none
	 */

	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());

		Log.printLine(CloudSim.clock() + ": " + getName()
				+ ": Cloud Resource List received with "
				+ getDatacenterIdsList().size() + " resource(s)");

		for (Integer datacenterId : getDatacenterIdsList()) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS,
					getId());
		}
	}

	/**
	 * Process the ack received due to a request for VM creation.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * 
	 * @pre ev != null
	 * @post $none
	 */

	/**
	 * Process a cloudlet return event.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * 
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		cloudletsSubmitted--;
		// all cloudlets executed
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0
				&& NetworkConstants.iteration > 10) {
			Log.printLine(CloudSim.clock() + ": " + getName()
					+ ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getAppCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmAndWorkflow(0);
			}

		}
	}

	/**
	 * Overrides this method when making a new and different type of Broker.
	 * This method is called by {@link #body()} for incoming unknown tags.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * 
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printLine(getName() + ".processOtherEvent(): "
					+ "Error - an event is null.");
			return;
		}

		Log.printLine(getName() + ".processOtherEvent(): "
				+ "Error - event unknown by this DatacenterBroker.");
	}

	/**
	 * Create the virtual machines in a datacenter and submit/schedule cloudlets
	 * to them.
	 * 
	 * @param datacenterId
	 *            Id of the chosen PowerDatacenter
	 * 
	 * @pre $none
	 * @post $none
	 */

	//not used
	protected void createVmAndWorkflow1(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the
		// next one
		int requestedVms = 0;

		// All host will have two VMs (assumption) VM is the minimum unit
		if (createvmflag) {
			CreateVMs(datacenterId);
			createvmflag = false;
		}

		// generate Application execution Requests
		for (int i = 0; i < 5; i++) {
			this.getAppCloudletList().add(
					new ExampleWorkflow(AppCloudlet.APP_Workflow,
							NetworkConstants.currentAppId, 0, 8, getId()));
			NetworkConstants.currentAppId++;

		}
		int k = 0;

		// schedule the application on VMs
		for (AppCloudlet app : this.getAppCloudletList()) {

			List<Integer> vmids = new ArrayList<Integer>();
			/*int numVms = linkDC.getVmList().size();
			UniformDistr ufrnd = new UniformDistr(0, numVms, 5);
			System.out.println("app.numbervm: " + app.numbervm);
			for (int i = 0; i < app.numbervm; i++) {

				int vmid = (int) ufrnd.sample();
				vmids.add(vmid);

			}*/
			System.out.println("app.numbervm: " + app.numbervm);
			for (int i = 0; i < 8; i++)
			{
				Integer vmId = (Integer) i;
				vmids.add(vmId);
			}
			if (vmids != null) {
				if (!vmids.isEmpty()) {

					app.createCloudletList(vmids);
					for (int i = 0; i < 5; i++) {
						app.clist.get(i).setUserId(getId());
						appCloudletRecieved.put(app.appID, app.numbervm);
						this.getCloudletSubmittedList().add(app.clist.get(i));
						cloudletsSubmitted++;

						// Sending cloudlet
						sendNow(getVmsToDatacentersMap().get(
								this.getVmList().get(0).getId()),
								CloudSimTags.CLOUDLET_SUBMIT, app.clist.get(i));
					}
					System.out.println("app" + (k++));
				}
			}

		}
		setAppCloudletList(new ArrayList<AppCloudlet>());
		if (NetworkConstants.iteration < 10) {

			NetworkConstants.iteration++;
			this.schedule(getId(), NetworkConstants.nexttime,
					CloudSimTags.NextCycle);
		}

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}

	/**
	 * Create the virtual machines in a datacenter and submit/schedule cloudlets
	 * to them.
	 * 
	 * @param datacenterId
	 *            Id of the chosen PowerDatacenter
	 * 
	 * @pre $none
	 * @post $none
	 */
	
	//not used
	protected void createVmAndWorkflow2(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the
		// next one
		int requestedVms = 0;

		// All host will have two VMs (assumption) VM is the minimum unit
		if (createvmflag) {
			CreateVMs(datacenterId);
			createvmflag = false;
		}

		// generate Application execution Requests
		for (int i = 0; i < 1; i++) {
			this.getAppCloudletList().add(
					new WorkflowApp(AppCloudlet.APP_Workflow,
							NetworkConstants.currentAppId, 0, 0, getId()));
			NetworkConstants.currentAppId++;

		}
		int k = 0;

		// schedule the application on VMs
		for (AppCloudlet app : this.getAppCloudletList()) {

			List<Integer> vmids = new ArrayList<Integer>();
			int numVms = linkDC.getVmList().size();
			UniformDistr ufrnd = new UniformDistr(0, numVms, 5);
			for (int i = 0; i < app.numbervm; i++) {

				int vmid = (int) ufrnd.sample();
				vmids.add(vmid);

			}

			if (vmids != null) {
				if (!vmids.isEmpty()) {

					app.createCloudletList(vmids);
					for (int i = 0; i < app.numbervm; i++) {
						app.clist.get(i).setUserId(getId());
						appCloudletRecieved.put(app.appID, app.numbervm);
						this.getCloudletSubmittedList().add(app.clist.get(i));
						cloudletsSubmitted++;

						// Sending cloudlet
						sendNow(getVmsToDatacentersMap().get(
								this.getVmList().get(0).getId()),
								CloudSimTags.CLOUDLET_SUBMIT, app.clist.get(i));
					}
					System.out.println("app" + (k++));
				}
			}

		}
		setAppCloudletList(new ArrayList<AppCloudlet>());
		if (NetworkConstants.iteration < 10) {

			NetworkConstants.iteration++;
			this.schedule(getId(), NetworkConstants.nexttime,
					CloudSimTags.NextCycle);
		}

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}
	
	//used
	private void createVMsInDatecenter(int datacenterId) {

		// All host will have two VMs (assumption) VM is the minimum unit
		if (createvmflag) 
		{
			CreateVMs(datacenterId);
			createvmflag = false;
		}
	}
	
	
	private void createWorkflow(double[][]workflowDataTransferMap, double[]workflowExecutionMI, ArrayList<Integer> taskToVmMap) 
	{
		// generate Application execution Requests
		for (int i = 0; i < 1; i++) 
		{
			/*this.getAppCloudletList().add(
					new WorkflowApp(AppCloudlet.APP_Workflow,
							NetworkConstants.currentAppId, 0, getVmList().size(), getId()));*/
			this.getAppCloudletList().add(
					new PSOWorkflow(AppCloudlet.APP_Workflow,
							NetworkConstants.currentAppId, 0, getVmList().size(), getId()));
			NetworkConstants.currentAppId++;

		}
		int k = 0;

		// schedule the application on VMs
		for (AppCloudlet app : this.getAppCloudletList()) 
		{

			System.out.println("app.numbervm: " + app.numbervm);
		
			if (taskToVmMap != null) 
			{
				if (!taskToVmMap.isEmpty()) 
				{
					app.createCloudletList2(workflowDataTransferMap,  workflowExecutionMI, taskToVmMap);
					for (int i = 0; i < workflowExecutionMI.length; i++) {
						app.clist.get(i).setUserId(getId());
						appCloudletRecieved.put(app.appID, app.numbervm);
						this.getCloudletSubmittedList().add(app.clist.get(i));
						cloudletsSubmitted++;

						// Sending cloudlet
						sendNow(getVmsToDatacentersMap().get(
								this.getVmList().get(0).getId()),
								CloudSimTags.CLOUDLET_SUBMIT, app.clist.get(i));
					}
					System.out.println("app" + (k++));
				}
			}

		}
		setAppCloudletList(new ArrayList<AppCloudlet>());
		if (NetworkConstants.iteration < 10) {

			NetworkConstants.iteration++;
			this.schedule(getId(), NetworkConstants.nexttime,
					CloudSimTags.NextCycle);
		}
	}

	/**
	 * Create the virtual machines in a datacenter and submit/schedule cloudlets
	 * to them.
	 * 
	 * @param datacenterId
	 *            Id of the chosen PowerDatacenter
	 * 
	 * @pre $none
	 * @post $none
	 */
	//used
	protected void createVmAndWorkflow(int datacenterId) {
		
		executionCost = new double[numHosts*2];
		for( int i = 0; i< numHosts*2; i++)
		{
			executionCost[i] = exeCost[i%8];
		}
		transferCost = new double[numHosts*2][numHosts*2];
		for(int i=0; i< executionCost.length;i++)
		{
			for(int j=0; j< executionCost.length;j++)
			{
				transferCost[i][j]=trCost[i%8][j%8];
			}
		}
		//mips = new double[numHosts*2];
		//for(int i=0; i<executionCost.length; i++)
			//mips[i]=1;
		
		createVMsInDatecenter(datacenterId);
		
		//System.out.println("hello create vm and workflow");
		/*double[][] workflowDataTransferMap = {{0, 0, 80 * 1024 * 1024}, 
				                              {0, 0, 80 * 1024 * 1024}, 
				                              {0, 0, 0}};*/
		//double[] workflowExecutionMI = {8000, 8000, 8000};
		
		//workflowExecutionMI shows number of million instructions executed by each cloudlet
		//double[] workflowExecutionMI = {8000, 6000, 7000, 9000, 10000, 9000, 6000, 7000, 9000, 8000};
		
		double[] millionInstructions = {8000, 6000, 7000, 9000, 10000, 9000, 6000, 7000, 9000, 8000};
		//workflowDataTransferMap gives data to be transfered from ith cloudlet to jth cloudlet
		/*
		double[][] workflowDataTransferMap = {{0, 80, 90, 100, 0, 0, 0, 0, 0, 0}, 
				{0, 0, 0, 0, 60, 0, 0, 0, 0, 0}, 
				{0, 0, 0, 0, 50, 70, 80, 0, 0, 0},
				{0, 0, 0, 0, 0, 50, 0, 60, 0, 0},
				{0, 0, 0, 0, 0, 0, 60, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 80, 90, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 100, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 50},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 90},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
		*/
		double[][] workflowDataTransfer = {/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 80, 90, 100, 0, 0, 0, 0, 0, 0}, 
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 60, 0, 0, 0, 0, 0}, 
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 50, 70, 80, 0, 0, 0},
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 0, 50, 0, 60, 0, 0},
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 0, 0, 60, 0, 0, 0},
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 0, 0, 0, 80, 90, 0},
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 0, 0, 0, 0, 100, 0},
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 50},
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 90},
				/*{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};//*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
		
		workflowExecutionMI = new double[numberOfTasks];
		workflowDataTransferMap = new double[numberOfTasks][numberOfTasks];
		
		
		//create workflowExecutionMI matrix for specified number of tasks
		for ( int i = 0; i < numberOfTasks; i++)
		{
			workflowExecutionMI[i] = millionInstructions[i % 10];
		}
		
		//create workflowDataTransferMap matrix
		for ( int i = 0; i< numberOfTasks; i++)
		{
			for ( int j = 0; j< numberOfTasks; j++)
			{
				if(i < 10 && j<10)
				workflowDataTransferMap[i][j] = workflowDataTransfer[i % 10][j%10] * 1024 * 1024;
				else
					workflowDataTransferMap[i][j]=0;
			}
		}
		/*
		for (int i=0; i<numberOfTasks; i++)
		{
			for (int j=0;j<numberOfTasks;j++)
			{
				System.out.print(workflowDataTransferMap[i][j]+ " ");
			}
			System.out.println();
		}
		*/
		/*
		//converting data to be transfered in MB
		for (int i = 0; i < workflowExecutionMI.length; i++)
		{
			for (int j = 0; j < workflowExecutionMI.length; j++)
			{
				workflowDataTransferMap[i][j] *= 1024 * 1024;
			}
		}
		*/
		//create object of PSO
		//psoScheduling = new PSO(workflowDataTransferMap, workflowExecutionMI, linkDC);
		
		LeaderoScheduling = new Leadero(workflowDataTransferMap, workflowExecutionMI, linkDC, numberOfTasks);
		ArrayList<Integer> taskToVmMap = LeaderoScheduling.runLeadero();
		
		//ArrayList<Integer> taskToVmMap = psoScheduling.runPSO();//returns bestPosition arraylist
		/*Iterator it=taskToVmMap.iterator();
		while(it.hasNext())
		{
			System.out.println("mapping is "+it.next());
		}
		*/
		
		/*
		 * Scheduling tasks depending on FCFS scheduling
		 */
		
		//ArrayList<Integer> taskToVmMap = new ArrayList<Integer>();
		//for(int i = 0; i < workflowExecutionMI.length; i++)
		//{
			//taskToVmMap.add(i % (executionCost.length));
		//}
		
		/*taskToVmMap.add(5);
		taskToVmMap.add(1);
		taskToVmMap.add(2);
		taskToVmMap.add(3);
		taskToVmMap.add(4);
		taskToVmMap.add(5);
		taskToVmMap.add(6);
		taskToVmMap.add(7);
		taskToVmMap.add(0);
		taskToVmMap.add(1);
		*/
		
	
	createWorkflow(workflowDataTransferMap, workflowExecutionMI, taskToVmMap);
		
		// send as much vms as possible for this datacenter before trying the
		// next one
		int requestedVms = 0;

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}
	
	private void CreateVMs(int datacenterId) {
		// two VMs per host
		//System.out.println("pointer is");
		//System.out.println("here" + linkDC.getHostList());
		int numVM = linkDC.getHostList().size() * NetworkConstants.maxhostVM ;
		System.out.println("numVM:" + numVM);
		

		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = NetworkConstants.HOST_PEs
				/ NetworkConstants.maxhostVM;
		String vmm = "Xen"; // VMM name

		//convert transferCost per hour to transferCost per second also the bandwidth cost
		for (int i = 0; i < numHosts*2; i++)
		{
			for (int j = 0; j < numHosts*2; j++)
			{
				transferCost[i][j] /= 3600;
			}
		}
		for (int i = 0; i < numVM; i++) 
		{
			// create VM
			NetworkVm vm = new NetworkVm(i, getId(), mips[i], pesNumber, ram,
					bw, size, vmm, new LeaderNetworkCloudletSpaceSharedScheduler(), executionCost[i], 
					transferCost[i]);
			linkDC.processVmCreateNetwork(vm);
			// add the VM to the vmList
			getVmList().add(vm);
			getVmsToDatacentersMap().put(i, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), i));
			
		}
	}

	//not used
	private void CreateVMs2(int datacenterId) {
		// two VMs per host
		System.out.println("pointer ");
		System.out.println("here" + linkDC.getHostList());
		int numVM = linkDC.getHostList().size() * NetworkConstants.maxhostVM;

		for (int i = 0; i < numVM; i++) {
			int vmid = i;
			int mips = 1;
			long size = 10000; // image size (MB)
			int ram = 512; // vm memory (MB)
			long bw = 1000;
			int pesNumber = NetworkConstants.HOST_PEs
					/ NetworkConstants.maxhostVM;
			String vmm = "Xen"; // VMM name

			// create VM
			NetworkVm vm = new NetworkVm(vmid, getId(), mips, pesNumber, ram,
					bw, size, vmm, new LeaderNetworkCloudletSpaceSharedScheduler());
			linkDC.processVmCreateNetwork(vm);
			// add the VM to the vmList
			getVmList().add(vm);
			getVmsToDatacentersMap().put(vmid, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmid));
		}
	
	}

	/**
	 * Submit cloudlets to the created VMs.
	 * 
	 * @pre $none
	 * @post $none /** Destroy the virtual machines running in datacenters.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
		for (Vm vm : getVmsCreatedList()) {
			Log.printLine(CloudSim.clock() + ": " + getName()
					+ ": Destroying VM #" + vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()),
					CloudSimTags.VM_DESTROY, vm);
		}

		getVmsCreatedList().clear();
	}

	/**
	 * Send an internal event communicating the end of the simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	private void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
		Log.printLine(getName() + " is shutting down...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmList() {
		return (List<T>) vmList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param vmList
	 *            the new vm list
	 */
	protected <T extends Vm> void setVmList(List<T> vmList) {
		this.vmList = vmList;
	}

	/**
	 * Gets the cloudlet list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet list
	 */
	@SuppressWarnings("unchecked")
	public <T extends NetworkCloudlet> List<T> getCloudletList() {
		return (List<T>) cloudletList;
	}

	/**
	 * Sets the cloudlet list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletList
	 *            the new cloudlet list
	 */
	protected <T extends NetworkCloudlet> void setCloudletList(
			List<T> cloudletList) {
		this.cloudletList = cloudletList;
	}

	@SuppressWarnings("unchecked")
	public <T extends AppCloudlet> List<T> getAppCloudletList() {
		return (List<T>) appCloudletList;
	}

	public <T extends AppCloudlet> void setAppCloudletList(
			List<T> appCloudletList) {
		this.appCloudletList = appCloudletList;
	}

	/**
	 * Gets the cloudlet submitted list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet submitted list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletSubmittedList() {
		return (List<T>) cloudletSubmittedList;
	}

	/**
	 * Sets the cloudlet submitted list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletSubmittedList
	 *            the new cloudlet submitted list
	 */
	protected <T extends Cloudlet> void setCloudletSubmittedList(
			List<T> cloudletSubmittedList) {
		this.cloudletSubmittedList = cloudletSubmittedList;
	}

	/**
	 * Gets the cloudlet received list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletReceivedList() {
		return (List<T>) cloudletReceivedList;
	}

	/**
	 * Sets the cloudlet received list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletReceivedList
	 *            the new cloudlet received list
	 */
	protected <T extends Cloudlet> void setCloudletReceivedList(
			List<T> cloudletReceivedList) {
		this.cloudletReceivedList = cloudletReceivedList;
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmsCreatedList() {
		return (List<T>) vmsCreatedList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param vmsCreatedList
	 *            the vms created list
	 */
	protected <T extends Vm> void setVmsCreatedList(List<T> vmsCreatedList) {
		this.vmsCreatedList = vmsCreatedList;
	}

	/**
	 * Gets the vms requested.
	 * 
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		return vmsRequested;
	}

	/**
	 * Sets the vms requested.
	 * 
	 * @param vmsRequested
	 *            the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		this.vmsRequested = vmsRequested;
	}

	/**
	 * Gets the vms acks.
	 * 
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 * 
	 * @param vmsAcks
	 *            the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		vmsAcks++;
	}

	/**
	 * Gets the vms destroyed.
	 * 
	 * @return the vms destroyed
	 */
	protected int getVmsDestroyed() {
		return vmsDestroyed;
	}

	/**
	 * Sets the vms destroyed.
	 * 
	 * @param vmsDestroyed
	 *            the new vms destroyed
	 */
	protected void setVmsDestroyed(int vmsDestroyed) {
		this.vmsDestroyed = vmsDestroyed;
	}

	/**
	 * Gets the datacenter ids list.
	 * 
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 * 
	 * @param datacenterIdsList
	 *            the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}

	/**
	 * Gets the vms to datacenters map.
	 * 
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 * 
	 * @param vmsToDatacentersMap
	 *            the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(
			Map<Integer, Integer> vmsToDatacentersMap) {
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}

	/**
	 * Gets the datacenter characteristics list.
	 * 
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
		return datacenterCharacteristicsList;
	}

	/**
	 * Sets the datacenter characteristics list.
	 * 
	 * @param datacenterCharacteristicsList
	 *            the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(
			Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}

	/**
	 * Gets the datacenter requested ids list.
	 * 
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		return datacenterRequestedIdsList;
	}

	/**
	 * Sets the datacenter requested ids list.
	 * 
	 * @param datacenterRequestedIdsList
	 *            the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(
			List<Integer> datacenterRequestedIdsList) {
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}

}
