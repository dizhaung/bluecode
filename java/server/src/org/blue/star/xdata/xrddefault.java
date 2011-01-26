/*****************************************************************************
 *
 * Blue Star, a Java Port of .
 * Last Modified : 3/20/2006
 *
 * License:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 *****************************************************************************/

package org.blue.star.xdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.blue.star.base.blue;
import org.blue.star.base.flapping;
import org.blue.star.base.notifications;
import org.blue.star.base.utils;
import org.blue.star.common.objects;
import org.blue.star.common.statusdata;
import org.blue.star.include.blue_h;
import org.blue.star.include.common_h;
import org.blue.star.include.locations_h;
import org.blue.star.include.objects_h;


public class xrddefault {

    /** Logger instance */
    private static Logger logger = LogManager.getLogger("org.blue.xdata.xrddefault");
    public static String cn = "org.blue.xdata.xrddefault"; 

    public static String xrddefault_retention_file = null;
    public static String xrddefault_temp_file = null;

/******************************************************************/
/********************* CONFIG INITIALIZATION  *********************/
/******************************************************************/

public static int xrddefault_grab_config_info(String main_config_file)
{

	/* initialize the location of the retention file */
    xrddefault_retention_file = locations_h.DEFAULT_RETENTION_FILE;
	xrddefault_temp_file = locations_h.DEFAULT_TEMP_FILE;

	/* open the main config file for reading */
    blue_h.mmapfile thefile=utils.file_functions.mmap_fopen(main_config_file);
	if( thefile == null ){
		logger.fatal("Error: Cannot open main configuration file '"+main_config_file+"' for reading!");
		return common_h.ERROR;
	        }

	/* read in all lines from the main config file */
    while( true ){
        
        /* read the next line */
        String input = utils.file_functions.mmap_fgets(thefile);
        if ( input == null )
            break;
        
        input = input.trim();
        
        /* skip blank lines and comments */
        if(input.length() == 0 ||  input.charAt(0) =='#' )
            continue;

        /* temp file definition */
        if( input.startsWith("temp_file="))
        {
            xrddefault_temp_file  = input.substring( input.indexOf( '=' ) + 1 ).trim();
        }

        /* retention file location */
        else if ( input.startsWith( "xrddefault_retention_file=") || input.startsWith( "state_retention_file=") )
            xrddefault_retention_file = input.substring( input.indexOf( '=' ) + 1 ).trim();

	        }

	/* free memory and close the file */
	utils.file_functions.mmap_fclose(thefile);

	/* save the retention file macro */
	blue.macro_x[blue_h.MACRO_RETENTIONDATAFILE]= xrddefault_retention_file;

	return common_h.OK;
    }


/******************************************************************/
/**************** DEFAULT STATE OUTPUT FUNCTION *******************/
/******************************************************************/

public static int xrddefault_save_state_information(String main_config_file){
//	char temp_buffer[MAX_INPUT_BUFFER];
//	char temp_file[MAX_FILENAME_LENGTH];
//	time_t current_time;
//	int result=OK;
//	FILE *fp=null;
//	host *temp_host=null;
//	service *temp_service=null;
//	int x, fd=0;

	logger.trace( "entering " + cn + ".xrddefault_save_state_information");

	/* grab config info */
	if( xrddefault_grab_config_info(main_config_file)== common_h.ERROR)
	{
		logger.fatal( "Error: Failed to grab configuration information for retention data!");
		return common_h.ERROR;
	}

	/* open a safe temp file for output */
    PrintWriter pw = null;
    File temp_file = null;
    try {
    temp_file = File.createTempFile( "retention", "file" );
    pw = new PrintWriter( new FileWriter( temp_file ));
    
    } catch (IOException ioE) {
        logger.fatal( "Error: Unable to open temp file '"+temp_file.toString() +"' for writing retention data!" , ioE );
        return common_h.ERROR;
        
    } 

//		snprintf(temp_buffer,sizeof(temp_buffer)-1,"Error: Could not open temp state retention file '%s' for writing!\n",temp_file);

	/* write version info to status file */
	pw.println( "########################################");
	pw.println( "#      NAGIOS STATE RETENTION FILE");
	pw.println( "#");
	pw.println( "# THIS FILE IS AUTOMATICALLY GENERATED");
	pw.println( "# BY NAGIOS.  DO NOT MODIFY THIS FILE!");
	pw.println( "########################################");
    pw.println( );
    
    long current_time = utils.currentTimeInSeconds();

	/* write file info */
	pw.println( "info {");
	pw.println( "\tcreated=" + current_time);
	pw.println( "\tversion=" + common_h.PROGRAM_VERSION);
	pw.println( "\t}");
    pw.println(); 
            
	/* save program state information */
	pw.println( "program {");
	pw.println( "\tmodified_host_attributes=" + blue.modified_host_process_attributes);
	pw.println( "\tmodified_service_attributes=" + blue.modified_service_process_attributes);
	pw.println( "\tenable_notifications=" + blue.enable_notifications);
	pw.println( "\tactive_service_checks_enabled=" + blue.execute_service_checks);
	pw.println( "\tpassive_service_checks_enabled=" + blue.accept_passive_service_checks);
	pw.println( "\tactive_host_checks_enabled=" + blue.execute_host_checks);
	pw.println( "\tpassive_host_checks_enabled=" + blue.accept_passive_host_checks);
	pw.println( "\tenable_event_handlers=" + blue.enable_event_handlers);
	pw.println( "\tobsess_over_services=" + blue.obsess_over_services);
	pw.println( "\tobsess_over_hosts=" + blue.obsess_over_hosts);
	pw.println( "\tcheck_service_freshness=" + blue.check_service_freshness);
	pw.println( "\tcheck_host_freshness=" + blue.check_host_freshness);
	pw.println( "\tenable_flap_detection=" + blue.enable_flap_detection);
	pw.println( "\tenable_failure_prediction=" + blue.enable_failure_prediction);
	pw.println( "\tprocess_performance_data=" + blue.process_performance_data);
	pw.println( "\tglobal_host_event_handler=" + ((blue.global_host_event_handler==null)?"":blue.global_host_event_handler));
	pw.println( "\tglobal_service_event_handler=" + ((blue.global_service_event_handler==null)?"":blue.global_service_event_handler));
	pw.println( "\t}");
    pw.println(); 
            
	/* save host state information */
	for( objects_h.host temp_host : (ArrayList<objects_h.host>) objects.host_list ){

		pw.println( "host {");
		pw.println( "\thost_name=" + temp_host.name);
		pw.println( "\tmodified_attributes=" + temp_host.modified_attributes);
		pw.println( "\tcheck_command=" + ((temp_host.host_check_command==null)?"":temp_host.host_check_command));
		pw.println( "\tevent_handler=" + ((temp_host.event_handler==null)?"":temp_host.event_handler));
		pw.println( "\thas_been_checked=" + temp_host.has_been_checked);
		pw.println( "\tcheck_execution_time=" + temp_host.execution_time);
		pw.println( "\tcheck_latency=" + temp_host.latency);
		pw.println( "\tcheck_type=" + temp_host.check_type);
		pw.println( "\tcurrent_state=" + temp_host.current_state);
		pw.println( "\tlast_state=" + temp_host.last_state);
		pw.println( "\tlast_hard_state=" + temp_host.last_hard_state);
		pw.println( "\tplugin_output=" + ((temp_host.plugin_output==null)?"":temp_host.plugin_output));
		pw.println( "\tperformance_data=" + ((temp_host.perf_data==null)?"":temp_host.perf_data));
		pw.println( "\tlast_check=" + temp_host.last_check);
		pw.println( "\tnext_check=" + temp_host.next_check);
		pw.println( "\tcurrent_attempt=" + temp_host.current_attempt);
		pw.println( "\tmax_attempts=" + temp_host.max_attempts);
		pw.println( "\tnormal_check_interval=" + temp_host.check_interval);
		pw.println( "\tstate_type=" + temp_host.state_type);
		pw.println( "\tlast_state_change=" + temp_host.last_state_change);
		pw.println( "\tlast_hard_state_change=" + temp_host.last_hard_state_change);
		pw.println( "\tlast_time_up=" + temp_host.last_time_up);
		pw.println( "\tlast_time_down=" + temp_host.last_time_down);
		pw.println( "\tlast_time_unreachable=" + temp_host.last_time_unreachable);
		pw.println( "\tnotified_on_down=" + temp_host.notified_on_down);
		pw.println( "\tnotified_on_unreachable=" + temp_host.notified_on_unreachable);
		pw.println( "\tlast_notification=" + temp_host.last_host_notification);
		pw.println( "\tcurrent_notification_number=" + temp_host.current_notification_number);
		pw.println( "\tnotifications_enabled=" + temp_host.notifications_enabled);
		pw.println( "\tproblem_has_been_acknowledged=" + temp_host.problem_has_been_acknowledged);
		pw.println( "\tacknowledgement_type=" + temp_host.acknowledgement_type);
		pw.println( "\tactive_checks_enabled=" + temp_host.checks_enabled);
		pw.println( "\tpassive_checks_enabled=" + temp_host.accept_passive_host_checks);
		pw.println( "\tevent_handler_enabled=" + temp_host.event_handler_enabled);
		pw.println( "\tflap_detection_enabled=" + temp_host.flap_detection_enabled);
		pw.println( "\tfailure_prediction_enabled=" + temp_host.failure_prediction_enabled);
		pw.println( "\tprocess_performance_data=" + temp_host.process_performance_data);
		pw.println( "\tobsess_over_host=" + temp_host.obsess_over_host);
		pw.println( "\tis_flapping=" + temp_host.is_flapping);
		pw.println( "\tpercent_state_change=" + temp_host.percent_state_change);

		pw.print( "\tstate_history=");
		for(int x=0;x<objects_h.MAX_STATE_HISTORY_ENTRIES;x++)
			pw.print( ((x>0)?",":"") + temp_host.state_history[(x+temp_host.state_history_index)%objects_h.MAX_STATE_HISTORY_ENTRIES]);
        pw.println( );

        pw.println( "\t}");
        pw.println();
                }

	/* save service state information */
	for( objects_h.service temp_service : (ArrayList<objects_h.service>) objects.service_list ){

		pw.println( "service {");
		pw.println( "\thost_name=" + temp_service.host_name);
		pw.println( "\tservice_description=" + temp_service.description);
		pw.println( "\tmodified_attributes=" + temp_service.modified_attributes);
		pw.println( "\tcheck_command=" + ((temp_service.service_check_command==null)?"":temp_service.service_check_command));
		pw.println( "\tevent_handler=" + ((temp_service.event_handler==null)?"":temp_service.event_handler));
		pw.println( "\thas_been_checked=" + temp_service.has_been_checked);
		pw.println( "\tcheck_execution_time=" + temp_service.execution_time);
		pw.println( "\tcheck_latency=" + temp_service.latency);
		pw.println( "\tcheck_type=" + temp_service.check_type);
		pw.println( "\tcurrent_state=" + temp_service.current_state);
		pw.println( "\tlast_state=" + temp_service.last_state);
		pw.println( "\tlast_hard_state=" + temp_service.last_hard_state);
		pw.println( "\tcurrent_attempt=" + temp_service.current_attempt);
		pw.println( "\tmax_attempts=" + temp_service.max_attempts);
		pw.println( "\tnormal_check_interval=" + temp_service.check_interval);
		pw.println( "\tretry_check_interval=" + temp_service.retry_interval);
		pw.println( "\tstate_type=" + temp_service.state_type);
		pw.println( "\tlast_state_change=" + temp_service.last_state_change);
		pw.println( "\tlast_hard_state_change=" + temp_service.last_hard_state_change);
		pw.println( "\tlast_time_ok=" + temp_service.last_time_ok);
		pw.println( "\tlast_time_warning=" + temp_service.last_time_warning);
		pw.println( "\tlast_time_unknown=" + temp_service.last_time_unknown);
		pw.println( "\tlast_time_critical=" + temp_service.last_time_critical);
		pw.println( "\tplugin_output=" + ((temp_service.plugin_output==null)?"":temp_service.plugin_output));
		pw.println( "\tperformance_data=" + ((temp_service.perf_data==null)?"":temp_service.perf_data));
		pw.println( "\tlast_check=" + temp_service.last_check);
		pw.println( "\tnext_check=" + temp_service.next_check);
		pw.println( "\tnotified_on_unknown=" + temp_service.notified_on_unknown);
		pw.println( "\tnotified_on_warning=" + temp_service.notified_on_warning);
		pw.println( "\tnotified_on_critical=" + temp_service.notified_on_critical);
		pw.println( "\tcurrent_notification_number=" + temp_service.current_notification_number);
		pw.println( "\tlast_notification=" + temp_service.last_notification);
		pw.println( "\tnotifications_enabled=" + temp_service.notifications_enabled);
		pw.println( "\tactive_checks_enabled=" + temp_service.checks_enabled);
		pw.println( "\tpassive_checks_enabled=" + temp_service.accept_passive_service_checks);
		pw.println( "\tevent_handler_enabled=" + temp_service.event_handler_enabled);
		pw.println( "\tproblem_has_been_acknowledged=" + temp_service.problem_has_been_acknowledged);
		pw.println( "\tacknowledgement_type=" + temp_service.acknowledgement_type);
		pw.println( "\tflap_detection_enabled=" + temp_service.flap_detection_enabled);
		pw.println( "\tfailure_prediction_enabled=" + temp_service.failure_prediction_enabled);
		pw.println( "\tprocess_performance_data=" + temp_service.process_performance_data);
		pw.println( "\tobsess_over_service=" + temp_service.obsess_over_service);
		pw.println( "\tis_flapping=" + temp_service.is_flapping);
		pw.println( "\tpercent_state_change=" + temp_service.percent_state_change);

		pw.print( "\tstate_history=");
		for( int x=0;x<objects_h.MAX_STATE_HISTORY_ENTRIES;x++)
			pw.print( ((x>0)?",":"") + temp_service.state_history[(x+temp_service.state_history_index)%objects_h.MAX_STATE_HISTORY_ENTRIES]);
		pw.println( );

		pw.println( "\t}");
        pw.println( );
	        }

    pw.close();
	/* move the temp file to the retention file (overwrite the old retention file) */
    if( utils.file_functions.my_rename( temp_file.toString(), xrddefault_retention_file) != 0 ){
        logger.fatal( "Error: Unable to update retention file '"+xrddefault_retention_file+"'!");
        return common_h.ERROR;
    }
    
    temp_file.delete();

	logger.trace( "exiting " + cn + ".xrddefault_save_state_information");
	return common_h.OK;
        }




/******************************************************************/
/***************** DEFAULT STATE INPUT FUNCTION *******************/
/******************************************************************/

public static int xrddefault_read_state_information(String main_config_file)
{
	
	String host_name=null;
	String service_description=null;
	int data_type=xrddefault_h.XRDDEFAULT_NO_DATA;
	objects_h.host temp_host = null;
	objects_h.service temp_service=null;
	int scheduling_info_is_ok=common_h.FALSE;

	logger.trace( "entering " + cn + ".xrddefault_read_state_information");

	/* grab config info */
	if(xrddefault_grab_config_info(main_config_file)== common_h.ERROR){
		logger.fatal( "Error: Failed to grab configuration information for retention data!");
		return common_h.ERROR;
	        }

	/* open the retention file for reading */
    blue_h.mmapfile thefile=utils.file_functions.mmap_fopen(xrddefault_retention_file);
	if( thefile == null )
		return common_h.ERROR;


	/* read all lines in the retention file */
    while( true ){

        /* read the next line */
        String input = utils.file_functions.mmap_fgets( thefile );
        if( input == null )
            break;

        input = input.trim();

        /* skip blank lines and comments */
        if( input.length() == 0 || input.charAt(0) =='#' )
            continue;

		else if(input.equals( "info {"))
			data_type=xrddefault_h.XRDDEFAULT_INFO_DATA;
		else if(input.equals( "program {"))
			data_type=xrddefault_h.XRDDEFAULT_PROGRAM_DATA;
		else if(input.equals( "host {"))
			data_type=xrddefault_h.XRDDEFAULT_HOST_DATA;
		else if(input.equals( "service {"))
			data_type=xrddefault_h.XRDDEFAULT_SERVICE_DATA;

		else if(input.equals( "}")){

			switch(data_type){

			case xrddefault_h.XRDDEFAULT_INFO_DATA:
				break;

			case xrddefault_h.XRDDEFAULT_PROGRAM_DATA:

				/* adjust modified attributes if necessary */
				if(blue.use_retained_program_state==common_h.FALSE){
					blue.modified_host_process_attributes=common_h.MODATTR_NONE;
					blue.modified_service_process_attributes= common_h.MODATTR_NONE;
				        }
				break;

			case xrddefault_h.XRDDEFAULT_HOST_DATA:

				if(temp_host!=null){

					/* adjust modified attributes if necessary */
					if(temp_host.retain_nonstatus_information==common_h.FALSE)
						temp_host.modified_attributes=common_h.MODATTR_NONE;

					/* calculate next possible notification time */
					if(temp_host.current_state!=blue_h.HOST_UP && temp_host.last_host_notification!=0)
						temp_host.next_host_notification=  notifications.get_next_host_notification_time(temp_host,temp_host.last_host_notification);

					/* update host status */
					statusdata.update_host_status(temp_host,common_h.FALSE);

					/* check for flapping */
					flapping.check_for_host_flapping(temp_host,common_h.FALSE);

					/* handle new vars added */
					if(temp_host.last_hard_state_change==0)
						temp_host.last_hard_state_change=temp_host.last_state_change;
				        }

				host_name=null;
				temp_host=null;
				break;

			case xrddefault_h.XRDDEFAULT_SERVICE_DATA:

				if(temp_service!=null){

					/* adjust modified attributes if necessary */
					if(temp_service.retain_nonstatus_information==common_h.FALSE)
						temp_service.modified_attributes=common_h.MODATTR_NONE;

					/* calculate next possible notification time */
					if(temp_service.current_state!=blue_h.STATE_OK && temp_service.last_notification!=0)
						temp_service.next_notification=notifications.get_next_service_notification_time(temp_service,temp_service.last_notification);

					/* fix old vars */
					if(temp_service.has_been_checked==common_h.FALSE && temp_service.state_type== common_h.SOFT_STATE)
						temp_service.state_type=common_h.HARD_STATE;

					/* update service status */
                    statusdata.update_service_status(temp_service,common_h.FALSE);

					/* check for flapping */
					flapping.check_for_service_flapping(temp_service,common_h.FALSE);
					
					/* handle new vars added */
					if(temp_service.last_hard_state_change==0)
						temp_service.last_hard_state_change=temp_service.last_state_change;
				        }

				host_name=null;
				service_description=null;
				temp_service=null;
                temp_host = null;
				break;

			default:
				break;
			        }

			data_type=xrddefault_h.XRDDEFAULT_NO_DATA;
		        }

		else if( data_type!=xrddefault_h.XRDDEFAULT_NO_DATA){

            String[] splitVarVal = input.split( "[=]", 2);
            if ( splitVarVal.length != 2 )
                continue;
			String var=splitVarVal[0];
			String val=splitVarVal[1];

			switch(data_type){

			case xrddefault_h.XRDDEFAULT_INFO_DATA:
				if(var.equals( "created")){
					long creation_time=strtoul(val,null,10);
					long current_time = utils.currentTimeInSeconds();
					if(current_time-creation_time<blue.retention_scheduling_horizon)
						scheduling_info_is_ok=common_h.TRUE;
					else
						scheduling_info_is_ok=common_h.FALSE;
				        }
				break;

			case xrddefault_h.XRDDEFAULT_PROGRAM_DATA:
				if(var.equals( "modified_host_attributes"))
					blue.modified_host_process_attributes=strtoul(val,null,10);
				else if(var.equals( "modified_service_attributes"))
					blue.modified_service_process_attributes=strtoul(val,null,10);
                
				if(blue.use_retained_program_state==common_h.TRUE){
					if(var.equals( "enable_notifications")){
						if( (blue.modified_host_process_attributes & common_h.MODATTR_NOTIFICATIONS_ENABLED) > 0 )
							blue.enable_notifications=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "active_service_checks_enabled")){
						if( ( blue.modified_service_process_attributes & common_h.MODATTR_ACTIVE_CHECKS_ENABLED) > 0 )
                            blue.execute_service_checks=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "passive_service_checks_enabled")){
						if( (blue.modified_service_process_attributes & common_h.MODATTR_PASSIVE_CHECKS_ENABLED) > 0 )
                            blue.accept_passive_service_checks=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "active_host_checks_enabled")){
						if( (blue.modified_host_process_attributes & common_h.MODATTR_ACTIVE_CHECKS_ENABLED) > 0 )
                            blue.execute_host_checks=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "passive_host_checks_enabled")){
						if( ( blue.modified_host_process_attributes & common_h.MODATTR_PASSIVE_CHECKS_ENABLED) > 0 )
                            blue.accept_passive_host_checks=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "enable_event_handlers")){
						if( ( blue.modified_host_process_attributes & common_h.MODATTR_EVENT_HANDLER_ENABLED) > 0 )
                            blue.enable_event_handlers=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "obsess_over_services")){
						if( ( blue.modified_service_process_attributes & common_h.MODATTR_OBSESSIVE_HANDLER_ENABLED) > 0 )
                            blue.obsess_over_services=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "obsess_over_hosts")){
						if( ( blue.modified_host_process_attributes & common_h.MODATTR_OBSESSIVE_HANDLER_ENABLED) > 0 )
                            blue.obsess_over_hosts=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "check_service_freshness")){
						if( ( blue.modified_service_process_attributes & common_h.MODATTR_FRESHNESS_CHECKS_ENABLED) > 0 )
                            blue.check_service_freshness=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "check_host_freshness")){
						if( ( blue.modified_host_process_attributes & common_h.MODATTR_FRESHNESS_CHECKS_ENABLED) > 0 )
                            blue.check_host_freshness=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "enable_flap_detection")){
						if( ( blue.modified_host_process_attributes & common_h.MODATTR_FLAP_DETECTION_ENABLED) > 0 )
                            blue.enable_flap_detection=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "enable_failure_prediction")){
						if( ( blue.modified_host_process_attributes & common_h.MODATTR_FAILURE_PREDICTION_ENABLED) > 0)
                            blue.enable_failure_prediction=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "process_performance_data")){
						if( ( blue.modified_host_process_attributes & common_h.MODATTR_PERFORMANCE_DATA_ENABLED) > 0 )
                            blue.process_performance_data=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
					        }
					else if(var.equals( "global_host_event_handler")){
					    if( (blue.modified_host_process_attributes & common_h.MODATTR_EVENT_HANDLER_COMMAND) > 0 ){
					        
					        /* make sure the check command still exists... */
                            String[] split = val.split ( "\\!" );
					        objects_h.command temp_command = objects.find_command( split[0] );
					        
					        if(temp_command!=null && val !=null){
                                blue.global_host_event_handler=val;
					        }
					    }
					}
					else if(var.equals( "global_service_event_handler")){
						if( ( blue.modified_service_process_attributes & common_h.MODATTR_EVENT_HANDLER_COMMAND) > 0 ){

							/* make sure the check command still exists... */
                            String[] split = val.split ( "\\!" );
                            objects_h.command temp_command= objects.find_command( split[0] );

							if(temp_command!=null && val !=null){
                                blue.global_service_event_handler=val;
							        }
						        }
					        }
				        }
				break;

			case xrddefault_h.XRDDEFAULT_HOST_DATA:
				if(var.equals( "host_name")){
                    host_name = var;
					temp_host = objects.find_host( host_name );
				        }
				else if(temp_host!=null){
					if(var.equals( "modified_attributes"))
						temp_host.modified_attributes=strtoul(val,null,10);
                    
					if(temp_host.retain_status_information==common_h.TRUE){
						if(var.equals( "has_been_checked"))
							temp_host.has_been_checked=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "check_execution_time"))
							temp_host.execution_time=strtod(val,null);
						else if(var.equals( "check_latency"))
							temp_host.latency=strtod(val,null);
						else if(var.equals( "check_type"))
							temp_host.check_type=atoi(val);
						else if(var.equals( "current_state"))
							temp_host.current_state=atoi(val);
						else if(var.equals( "last_state"))
							temp_host.last_state=atoi(val);
						else if(var.equals( "last_hard_state"))
							temp_host.last_hard_state=atoi(val);
						else if(var.equals( "plugin_output"))
							temp_host.plugin_output = val;
						else if(var.equals( "performance_data"))
							temp_host.perf_data = val;
						else if(var.equals( "last_check"))
							temp_host.last_check=strtoul(val,null,10);
						else if(var.equals( "next_check")){
							if(blue.use_retained_scheduling_info==common_h.TRUE && scheduling_info_is_ok==common_h.TRUE)
								temp_host.next_check=strtoul(val,null,10);
						        }
						else if(var.equals( "current_attempt"))
							temp_host.current_attempt=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "state_type"))
							temp_host.state_type=atoi(val);
						else if(var.equals( "last_state_change"))
							temp_host.last_state_change=strtoul(val,null,10);
						else if(var.equals( "last_hard_state_change"))
							temp_host.last_hard_state_change=strtoul(val,null,10);
						else if(var.equals( "last_time_up"))
							temp_host.last_time_up=strtoul(val,null,10);
						else if(var.equals( "last_time_down"))
							temp_host.last_time_down=strtoul(val,null,10);
						else if(var.equals( "last_time_unreachable"))
							temp_host.last_time_unreachable=strtoul(val,null,10);
						else if(var.equals( "notified_on_down"))
							temp_host.notified_on_down=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "notified_on_unreachable"))
							temp_host.notified_on_unreachable=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "last_notification"))
							temp_host.last_host_notification=strtoul(val,null,10);
						else if(var.equals( "current_notification_number"))
							temp_host.current_notification_number=atoi(val);
						else if(var.equals( "state_history")){
                            String[] split = val.split ( "[,]");
                            for ( int x = 0; x < split.length && x < objects_h.MAX_STATE_HISTORY_ENTRIES ; x++ )
									temp_host.state_history[x]=atoi(split[0]);
							temp_host.state_history_index=0;
                        }
					}
                    
					if(temp_host.retain_nonstatus_information==common_h.TRUE){
						if(var.equals( "problem_has_been_acknowledged"))
							temp_host.problem_has_been_acknowledged=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "acknowledgement_type"))
							temp_host.acknowledgement_type=atoi(val);
						else if(var.equals( "notifications_enabled")){
							if ( (temp_host.modified_attributes & common_h.MODATTR_NOTIFICATIONS_ENABLED) > 0 )
								temp_host.notifications_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "active_checks_enabled")){
							if ( (temp_host.modified_attributes & common_h.MODATTR_ACTIVE_CHECKS_ENABLED) > 0  ) 
								temp_host.checks_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "passive_checks_enabled")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_PASSIVE_CHECKS_ENABLED) > 0 )
								temp_host.accept_passive_host_checks=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "event_handler_enabled")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_EVENT_HANDLER_ENABLED) > 0 )
								temp_host.event_handler_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "flap_detection_enabled")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_FLAP_DETECTION_ENABLED) > 0 )
								temp_host.flap_detection_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "failure_prediction_enabled")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_FAILURE_PREDICTION_ENABLED) >  0 )
								temp_host.failure_prediction_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "process_performance_data")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_PERFORMANCE_DATA_ENABLED) > 0 )
								temp_host.process_performance_data=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "obsess_over_host")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_OBSESSIVE_HANDLER_ENABLED) >  0)
								temp_host.obsess_over_host=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "check_command")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_CHECK_COMMAND )  > 0 ){

								/* make sure the check command still exists... */
                                String[] split = val.split ( "\\!" );
                                objects_h.command temp_command= objects.find_command( split[0] );

								if(temp_command!=null && val!=null){
									temp_host.host_check_command=val;
								        }
							        }
						        }
						else if(var.equals( "event_handler")){
							if( ( temp_host.modified_attributes & common_h.MODATTR_EVENT_HANDLER_COMMAND) > 0 ){

								/* make sure the check command still exists... */
                                String[] split = val.split ( "\\!" );
                                objects_h.command temp_command= objects.find_command( split[0] );

								if(temp_command!=null && val!=null){
									temp_host.event_handler=val;
								        }
							        }
						        }
						else if(var.equals( "normal_check_interval")){
							if( ( ( temp_host.modified_attributes & common_h.MODATTR_NORMAL_CHECK_INTERVAL ) > 0 ) && atoi(val)>=0)
								temp_host.check_interval=atoi(val);
						        }
						else if(var.equals( "max_attempts")){
							if( ( ( temp_host.modified_attributes & common_h.MODATTR_MAX_CHECK_ATTEMPTS ) > 0 )&& atoi(val)>=1){
								
								temp_host.max_attempts=atoi(val);

								/* adjust current attempt number if in a hard state */
								if(temp_host.state_type==common_h.HARD_STATE && temp_host.current_state!= blue_h.HOST_UP && temp_host.current_attempt>1)
									temp_host.current_attempt=temp_host.max_attempts;
							        }
						        }
					        }

				        }
				break;

			case xrddefault_h.XRDDEFAULT_SERVICE_DATA:
				if(var.equals( "host_name")){
                    host_name = val;
					temp_service= objects.find_service( val,service_description);
				        }
				else if(var.equals( "service_description")){
					service_description=val;
					temp_service= objects.find_service(host_name,service_description);
				        }
				else if(temp_service!=null){
					if(var.equals( "modified_attributes"))
						temp_service.modified_attributes=strtoul(val,null,10);
                    
					if(temp_service.retain_status_information==common_h.TRUE) {
						if(var.equals( "has_been_checked"))
							temp_service.has_been_checked=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "check_execution_time"))
							temp_service.execution_time=strtod(val,null);
						else if(var.equals( "check_latency"))
							temp_service.latency=strtod(val,null);
						else if(var.equals( "check_type"))
							temp_service.check_type=atoi(val);
						else if(var.equals( "current_state"))
							temp_service.current_state=atoi(val);
						else if(var.equals( "last_state"))
							temp_service.last_state=atoi(val);
						else if(var.equals( "last_hard_state"))
							temp_service.last_hard_state=atoi(val);
						else if(var.equals( "current_attempt"))
							temp_service.current_attempt=atoi(val);
						else if(var.equals( "state_type"))
							temp_service.state_type=atoi(val);
						else if(var.equals( "last_state_change"))
							temp_service.last_state_change=strtoul(val,null,10);
						else if(var.equals( "last_hard_state_change"))
							temp_service.last_hard_state_change=strtoul(val,null,10);
						else if(var.equals( "last_time_ok"))
							temp_service.last_time_ok=strtoul(val,null,10);
						else if(var.equals( "last_time_warning"))
							temp_service.last_time_warning=strtoul(val,null,10);
						else if(var.equals( "last_time_unknown"))
							temp_service.last_time_unknown=strtoul(val,null,10);
						else if(var.equals( "last_time_critical"))
							temp_service.last_time_critical=strtoul(val,null,10);
						else if(var.equals( "plugin_output"))
							temp_service.plugin_output = val;
						else if(var.equals( "performance_data"))
							temp_service.perf_data = val;
						else if(var.equals( "last_check"))
							temp_service.last_check=strtoul(val,null,10);
						else if(var.equals( "next_check")){
							if(blue.use_retained_scheduling_info==common_h.TRUE && scheduling_info_is_ok==common_h.TRUE)
								temp_service.next_check=strtoul(val,null,10);
						        }
						else if(var.equals( "notified_on_unknown"))
							temp_service.notified_on_unknown=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "notified_on_warning"))
							temp_service.notified_on_warning=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "notified_on_critical"))
							temp_service.notified_on_critical=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "current_notification_number"))
							temp_service.current_notification_number=atoi(val);
						else if(var.equals( "last_notification"))
							temp_service.last_notification=strtoul(val,null,10);

						else if(var.equals( "state_history")){
						    String[] split = val.split ( "[,]");
						    for ( int x = 0; x < split.length && x < objects_h.MAX_STATE_HISTORY_ENTRIES ; x++ )
						        temp_service.state_history[x]=atoi(split[0]);
						    temp_service.state_history_index=0;
						}
					}
                    
					if(temp_service.retain_nonstatus_information==common_h.TRUE){
						if(var.equals( "problem_has_been_acknowledged"))
							temp_service.problem_has_been_acknowledged=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						else if(var.equals( "acknowledgement_type"))
							temp_service.acknowledgement_type=atoi(val);
						else if(var.equals( "notifications_enabled")){
							if( ( temp_service.modified_attributes & common_h.MODATTR_NOTIFICATIONS_ENABLED) > 0 )
								temp_service.notifications_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "active_checks_enabled")){
							if(( temp_service.modified_attributes & common_h.MODATTR_ACTIVE_CHECKS_ENABLED) > 0 )
								temp_service.checks_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "passive_checks_enabled")){
							if( ( temp_service.modified_attributes & common_h.MODATTR_PASSIVE_CHECKS_ENABLED) > 0 ) 
								temp_service.accept_passive_service_checks=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "event_handler_enabled")){
							if(( temp_service.modified_attributes & common_h.MODATTR_EVENT_HANDLER_ENABLED) > 0 )
								temp_service.event_handler_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "flap_detection_enabled")){
							if( ( temp_service.modified_attributes & common_h.MODATTR_FLAP_DETECTION_ENABLED) > 0 )
								temp_service.flap_detection_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "failure_prediction_enabled")){
							if( ( temp_service.modified_attributes & common_h.MODATTR_FAILURE_PREDICTION_ENABLED) > 0 )
								temp_service.failure_prediction_enabled=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "process_performance_data")){
							if( ( temp_service.modified_attributes & common_h.MODATTR_PERFORMANCE_DATA_ENABLED) > 0 )
								temp_service.process_performance_data=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "obsess_over_service")){
							if( ( temp_service.modified_attributes & common_h.MODATTR_OBSESSIVE_HANDLER_ENABLED) > 0 )
								temp_service.obsess_over_service=(atoi(val)>0)?common_h.TRUE:common_h.FALSE;
						        }
						else if(var.equals( "check_command")){
							if( (temp_service.modified_attributes & common_h.MODATTR_CHECK_COMMAND) > 0 ){

								/* make sure the check command still exists... */
                                String[] split = val.split ( "\\!" );
                                objects_h.command temp_command= objects.find_command( split[0] );

								if(temp_command!=null && val !=null){
									temp_service.service_check_command=val;
								        }
							        }
						        }
						else if(var.equals( "event_handler")){
							if( ( temp_service.modified_attributes & common_h.MODATTR_EVENT_HANDLER_COMMAND) > 0 ){

								/* make sure the check command still exists... */
                                String[] split = val.split ( "\\!" );
                                objects_h.command temp_command= objects.find_command( split[0] );

								if(temp_command!=null && val!=null){
									temp_service.event_handler=val;
								        }
							        }
						        }
						else if(var.equals( "normal_check_interval")){
							if( ( ( temp_service.modified_attributes & common_h.MODATTR_NORMAL_CHECK_INTERVAL ) > 0 )&& atoi(val)>=0)
								temp_service.check_interval=atoi(val);
						        }
						else if(var.equals( "retry_check_interval")){
							if( ( ( temp_service.modified_attributes & common_h.MODATTR_RETRY_CHECK_INTERVAL ) > 0 ) && atoi(val)>=0)
								temp_service.retry_interval=atoi(val);
						        }
						else if(var.equals( "max_attempts")){
							if( ( ( temp_service.modified_attributes & common_h.MODATTR_MAX_CHECK_ATTEMPTS ) > 0 ) && atoi(val)>=1){
								
								temp_service.max_attempts=atoi(val);

								/* adjust current attempt number if in a hard state */
								if(temp_service.state_type==common_h.HARD_STATE && temp_service.current_state!=blue_h.STATE_OK && temp_service.current_attempt>1)
									temp_service.current_attempt=temp_service.max_attempts;
							        }
						        }
					        }
				        }
				break;

			default:
				break;
			        }

		        }
	        }

	/* free memory and close the file */
	utils.file_functions.mmap_fclose(thefile);

	logger.trace( "exiting " + cn + ".xrddefault_read_state_information");

	return common_h.OK;
        }


private static int atoi(String value) {
    try {
        return Integer.parseInt(value);
    } catch ( NumberFormatException nfE ) {
       logger.error("warning: " + nfE.getMessage(), nfE);
        return 0;
    }
}

private static double strtod(String value, Object ignore ) {
    try {
        return Double.parseDouble(value);
    } catch ( NumberFormatException nfE ) {
       logger.error("warning: " + nfE.getMessage(), nfE);
        return 0.0;
    }
}

private static long strtoul(String value, Object ignore, int base ) {
    try {
        return Long.parseLong(value);
    } catch ( NumberFormatException nfE ) {
       logger.error("warning: " + nfE.getMessage(), nfE);
        return 0L;
    }
}

}