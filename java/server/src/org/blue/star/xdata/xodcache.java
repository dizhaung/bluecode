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

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.blue.star.common.objects;
import org.blue.star.include.common_h;
import org.blue.star.include.objects_h;

public class xodcache
{
   private static Logger logger = LogManager.getLogger("org.blue.xdata.xodcache");

   public static String cn = "org.blue.xdata.xodcache";

   /**
    * Method that writes all object information to a cache file. This cache file is used by the 
    * web interface to produce all information shown to the user.
    * 
    * @param = String cache_file, the name of the cache file.
    * 
    * @return int, common_h.OK if everything went to plan, common_h.ERROR otherwise.
    */

   public static synchronized int cache_objects(String cache_file)
   {
      logger.trace("entering " + cn + ".cache_objects");

      /* open the cache file for writing */
      PrintWriter pw;

      try
      {
         pw = new PrintWriter(cache_file);
      }
      catch (Exception e)
      {
         logger.warn("Warning: Could not open object cache file '" + cache_file + "' for writing!", e);
         return common_h.ERROR;
      }

      /* write header to cache file */
      pw.println("########################################");
      pw.println("#       BLUE OBJECT LIVE CACHE FILE");
      pw.println("#");
      pw.println("# THIS FILE IS AUTOMATICALLY GENERATED");
      pw.println("# BY BLUE.  DO NOT MODIFY THIS FILE!");
      pw.println("#");
      pw.println("# Created: " + DateFormat.getDateInstance().format(new Date()));
      pw.println("########################################");
      pw.println();

      /* cache timeperiods */
      for (objects_h.timeperiod temp_timeperiod : objects.timeperiod_list)
	  writeTimePeriod( pw, temp_timeperiod) ;

      /* cache commands */
      for (objects_h.command temp_command : objects.command_list)
	  writeCommand( pw, temp_command );

      /* cache contactgroups */
      for (objects_h.contactgroup temp_contactgroup : objects.contactgroup_list)
	  writeContactGroup( pw, temp_contactgroup );

      /* cache hostgroups */
      for (objects_h.hostgroup temp_hostgroup : objects.hostgroup_list)
	  writeHostGroup( pw, temp_hostgroup );

      /* cache servicegroups */
      for (objects_h.servicegroup temp_servicegroup : objects.servicegroup_list)
	  writeServiceGroup( pw, temp_servicegroup );

      /* cache contacts */
      for (objects_h.contact temp_contact : objects.contact_list)
	  writeContact( pw, temp_contact );
      
      /* cache hosts */
      for (objects_h.host temp_host : objects.host_list)
	  writeHost( pw, temp_host );

      /* cache services */
      for (objects_h.service temp_service : objects.service_list)
	  writeService( pw, temp_service );

      /* cache service dependencies */
      for (objects_h.servicedependency temp_servicedependency : objects.servicedependency_list)
	  writeServiceDependency( pw, temp_servicedependency );

      /* cache service escalations */
      for (objects_h.serviceescalation temp_serviceescalation : objects.serviceescalation_list) 
	  writeServiceEscalation( pw, temp_serviceescalation );
      
      /* cache host dependencies */
      for (objects_h.hostdependency temp_hostdependency : objects.hostdependency_list)
	  writeHostDependency( pw, temp_hostdependency );


      /* cache host escalations */
      for (objects_h.hostescalation temp_hostescalation : objects.hostescalation_list)
	  writeHostEscalation( pw, temp_hostescalation );
      
      /* cache host extended info */
      for (objects_h.hostextinfo temp_hostextinfo : objects.hostextinfo_list)
	  writeHostExtInfo( pw, temp_hostextinfo );

      /* cache service extended info */
      for (objects_h.serviceextinfo temp_serviceextinfo : objects.serviceextinfo_list)
	  writeServiceExtInfo(pw, temp_serviceextinfo );
      
      pw.close();

      logger.trace("exiting " + cn + ".cache_objects");
      return common_h.OK;
   }
   
   public static void writeTimePeriod( PrintWriter pw, objects_h.timeperiod temp_timeperiod )
   {
       String[] days = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};

      define(pw,"timeperiod");
      entry(pw,"timeperiod_name",temp_timeperiod.name);
      entry( pw, "alias", temp_timeperiod.alias );

      // writing out \t [monday|...] \t HH:MM-HH:MM
      for (int x = 0; x < 7; x++) {
         if (temp_timeperiod.days[x] != null) {
            for (objects_h.timerange range : temp_timeperiod.days[x]) {
                xodcache.entry( pw, days[x], range.toString() );
            }
         }
      }
      end_define( pw );
   }

   public static void writeCommand( PrintWriter pw, objects_h.command temp_command )
   {
      define( pw, "command");
      entry( pw, "command_name", temp_command.name );
      entry( pw, "command_line", temp_command.command_line);
      end_define(pw);
   }

   public static void writeContactGroup( PrintWriter pw, objects_h.contactgroup temp_contactgroup )
   {
      define( pw, "contactgroup");
      entry( pw, "contactgroup_name", temp_contactgroup.group_name  );
      entry( pw, "alias", temp_contactgroup.alias  );
      list(pw, "members", temp_contactgroup.members);
      end_define(pw) ;
   }

   public static void writeHostGroup( PrintWriter pw, objects_h.hostgroup temp_hostgroup )
   {
      define( pw, "hostgroup");
      entry( pw, "hostgroup_name", temp_hostgroup.group_name );
      entry( pw, "alias", temp_hostgroup.alias );
      list(pw, "members", temp_hostgroup.members);
      end_define( pw );
   }

   public static void writeServiceGroup( PrintWriter pw, objects_h.servicegroup temp_servicegroup )
   {
       define( pw, "servicegroup");
       entry( pw, "servicegroup_name", temp_servicegroup.group_name  );
       entry( pw, "alias", temp_servicegroup.alias );
       list(pw, "members", temp_servicegroup.members);
       end_define( pw );
    }
   
   public static void writeContact( PrintWriter pw, objects_h.contact temp_contact ) 
   {
       define( pw, "contact");
       entry( pw, "contact_name", temp_contact.name );
       entry( pw, "alias", temp_contact.alias );
       entry( pw, "service_notification_period", temp_contact.service_notification_period );
       entry( pw, "host_notification_period", temp_contact.host_notification_period );

       pw.print("\tservice_notification_options\t");
       int x = 0;
       x += option( pw, temp_contact.notify_on_service_warning , "w" , x );
       x += option( pw, temp_contact.notify_on_service_unknown , "u" , x );
       x += option( pw, temp_contact.notify_on_service_critical , "c" , x );
       x += option( pw, temp_contact.notify_on_service_recovery , "r" , x );
       x += option( pw, temp_contact.notify_on_service_flapping , "f" , x );
       end_options( pw, x );
       
       pw.print("\thost_notification_options\t");
       x = 0;
       x += option( pw, temp_contact.notify_on_host_down , "d" , x );
       x += option( pw, temp_contact.notify_on_host_unreachable , "u" , x );
       x += option( pw, temp_contact.notify_on_host_recovery , "r" , x );
       x += option( pw, temp_contact.notify_on_host_flapping , "f" , x );
       end_options( pw, x );
       
       list(pw, "service_notification_commands", temp_contact.service_notification_commands);
       list(pw, "host_notification_commands", temp_contact.host_notification_commands);
       entry( pw, "email", temp_contact.email );
       entry( pw, "pager", temp_contact.pager );
       end_define( pw );
    }
   
   
   public static void writeHost( PrintWriter pw, objects_h.host temp_host )
   {
       define( pw, "host");
       entry( pw, "host_name", temp_host.name );
       entry( pw, "alias", temp_host.alias );
       entry( pw, "address", temp_host.address );       
       list( pw, "parents", temp_host.parent_hosts );
       entry( pw, "check_period", temp_host.check_period );
       entry( pw, "check_command", temp_host.host_check_command  );
       entry( pw, "event_handler", temp_host.event_handler );
       list(pw, "contact_groups", temp_host.contact_groups);
       entry( pw, "notification_period", temp_host.notification_period );
       entry( pw, "failure_prediction_options", temp_host.failure_prediction_options );
       entry( pw, "check_interval" , temp_host.check_interval);
       entry( pw, "max_check_attempts" , temp_host.max_attempts);
       entry( pw, "active_checks_enabled" , temp_host.checks_enabled);
       entry( pw, "passive_checks_enabled" , temp_host.accept_passive_host_checks);
       entry( pw, "obsess_over_host" , temp_host.obsess_over_host);
       entry( pw, "event_handler_enabled" , temp_host.event_handler_enabled);
       entry( pw, "low_flap_threshold" , temp_host.low_flap_threshold);
       entry( pw, "high_flap_threshold" , temp_host.high_flap_threshold);
       entry( pw, "flap_detection_enabled" , temp_host.flap_detection_enabled);
       entry( pw, "freshness_threshold" , temp_host.freshness_threshold);
       entry( pw, "check_freshness" , temp_host.check_freshness);

       pw.print("\tnotification_options\t");
       int x = 0;
       x += option( pw, temp_host.notify_on_down , "d", x );
       x += option( pw, temp_host.notify_on_unreachable ,"u", x );
       x += option( pw, temp_host.notify_on_recovery , "r", x );
       x += option( pw, temp_host.notify_on_flapping , "f", x );
       end_options( pw, x );
       
       entry(pw, "notifications_enabled" , temp_host.notifications_enabled);
       entry(pw, "notification_interval" , temp_host.notification_interval);

       pw.print("\tstalking_options\t");
       x = 0;
       x += option( pw, temp_host.stalk_on_up , "o", x);
       x += option( pw, temp_host.stalk_on_down, "d", x );
       x += option( pw, temp_host.stalk_on_unreachable, "u", x );
       end_options( pw, x );
       
       entry( pw, "process_perf_data" , temp_host.process_performance_data);
       entry( pw, "failure_prediction_enabled" , temp_host.failure_prediction_enabled);
       entry( pw, "retain_status_information" , temp_host.retain_status_information);
       entry( pw, "retain_nonstatus_information" , temp_host.retain_nonstatus_information);

       end_define( pw );
    }
   

   public static void writeService( PrintWriter pw, objects_h.service temp_service )
   {
	   define( pw, "service");
       entry( pw, "host_name" , temp_service.host_name);
       entry( pw, "service_description" , temp_service.description);
       entry( pw, "check_period" , temp_service.check_period);
       entry( pw, "check_command" , temp_service.service_check_command);
       entry( pw, "event_handler" , temp_service.event_handler);
       list(pw, "contact_groups", temp_service.contact_groups);
       entry( pw, "notification_period" , temp_service.notification_period);
       entry( pw, "failure_prediction_options" , temp_service.failure_prediction_options);
       entry( pw, "normal_check_interval" , temp_service.check_interval);
       entry( pw, "retry_check_interval" , temp_service.retry_interval);
       entry( pw, "max_check_attempts" , temp_service.max_attempts);
       entry( pw, "is_volatile" , temp_service.is_volatile);
       entry( pw, "parallelize_check" , temp_service.parallelize);
       entry( pw, "active_checks_enabled" , temp_service.checks_enabled);
       entry( pw, "passive_checks_enabled" , temp_service.accept_passive_service_checks);
       entry( pw, "obsess_over_service" , temp_service.obsess_over_service);
       entry( pw, "event_handler_enabled" , temp_service.event_handler_enabled);
       entry( pw, "low_flap_threshold" , temp_service.low_flap_threshold);
       entry( pw, "high_flap_threshold" , temp_service.high_flap_threshold);
       entry( pw, "flap_detection_enabled" , temp_service.flap_detection_enabled);
       entry( pw, "freshness_threshold" , temp_service.freshness_threshold);
       entry( pw, "check_freshness" , temp_service.check_freshness);

       pw.print("\tnotification_options\t");
       int x = 0;
       x += option( pw, temp_service.notify_on_unknown , "u", x );
       x += option( pw, temp_service.notify_on_warning , "w", x );
       x += option( pw, temp_service.notify_on_critical , "c", x );
       x += option( pw, temp_service.notify_on_recovery , "r", x );
       x += option( pw, temp_service.notify_on_flapping , "f", x );
       end_options( pw, x );
       
       entry( pw, "notifications_enabled" , temp_service.notifications_enabled);
       entry( pw, "notification_interval" , temp_service.notification_interval);

       pw.print("\tstalking_options\t");
       x = 0;
       x += option( pw, temp_service.stalk_on_ok , "o", x );
       x += option( pw, temp_service.stalk_on_unknown , "u", x );
       x += option( pw, temp_service.stalk_on_warning , "w", x );
       x += option( pw, temp_service.stalk_on_critical ,"c", x );
       end_options( pw, x );
       
       entry( pw, "process_perf_data" , temp_service.process_performance_data);
       entry( pw, "failure_prediction_enabled" , temp_service.failure_prediction_enabled);
       entry( pw, "retain_status_information" , temp_service.retain_status_information);
       entry( pw, "retain_nonstatus_information" , temp_service.retain_nonstatus_information);
       end_define( pw );
   }
   
   public static void writeServiceDependency( PrintWriter pw,  objects_h.servicedependency temp_servicedependency )
   {
      define( pw, "servicedependency");
      entry( pw, "host_name" , temp_servicedependency.host_name);
      entry( pw, "service_description" , temp_servicedependency.service_description);
      entry( pw, "dependent_host_name" , temp_servicedependency.dependent_host_name);
      entry( pw, "dependent_service_description" , temp_servicedependency.dependent_service_description);
      entry( pw, "inherits_parent" , temp_servicedependency.inherits_parent);

      if (temp_servicedependency.dependency_type == common_h.EXECUTION_DEPENDENCY)
      {
         pw.print("\tnotification_failure_options\t");
         int x = 0;
         x += option( pw, temp_servicedependency.fail_on_ok , "o", x );
         x += option( pw, temp_servicedependency.fail_on_unknown ,"u", x );
         x += option( pw, temp_servicedependency.fail_on_warning ,"w", x );
         x += option( pw, temp_servicedependency.fail_on_critical ,"c", x );
         x += option( pw, temp_servicedependency.fail_on_pending , "p", x );
         end_options(pw, x );
      }
      
      if (temp_servicedependency.dependency_type == common_h.NOTIFICATION_DEPENDENCY)
      {
         pw.print("\texecution_failure_options\t");
         int x = 0;
         x += option( pw, temp_servicedependency.fail_on_ok , "o", x );
         x += option( pw, temp_servicedependency.fail_on_unknown , "u", x );
         x += option( pw, temp_servicedependency.fail_on_warning , "w", x );
         x += option( pw, temp_servicedependency.fail_on_critical, "c", x );
         x += option( pw, temp_servicedependency.fail_on_pending , "p", x );
         end_options( pw, x );
      }
      
      end_define( pw );
   }

   public static void writeServiceEscalation( PrintWriter pw, objects_h.serviceescalation temp_serviceescalation )
   {
	  define ( pw, "serviceescalation");
	  entry( pw, "host_name" , temp_serviceescalation.host_name);
	  entry( pw, "service_description" , temp_serviceescalation.description);
	  entry( pw, "first_notification" , temp_serviceescalation.first_notification);
	  entry( pw, "last_notification" , temp_serviceescalation.last_notification);
	  entry( pw, "notification_interval" , temp_serviceescalation.notification_interval);
	  entry( pw, "escalation_period" , temp_serviceescalation.escalation_period);

	  pw.print("\tescalation_options\t");
	  int x = 0;
	  x += option( pw, temp_serviceescalation.escalate_on_warning , "w", x );
	  x += option( pw, temp_serviceescalation.escalate_on_unknown , "u", x );
	  x += option( pw, temp_serviceescalation.escalate_on_critical, "c", x );
	  x += option( pw, temp_serviceescalation.escalate_on_recovery, "r", x );
	  end_options( pw, x );

	  list(pw, "contact_groups", temp_serviceescalation.contact_groups);
	  end_define( pw );
   }

   public static void writeHostDependency(PrintWriter pw, objects_h.hostdependency temp_hostdependency) {
       define(pw, "hostdependency");
       entry(pw, "host_name", temp_hostdependency.host_name);
       entry(pw, "dependent_host_name", temp_hostdependency.dependent_host_name);
       entry(pw, "inherits_parent", temp_hostdependency.inherits_parent);

       if (temp_hostdependency.dependency_type == common_h.NOTIFICATION_DEPENDENCY)
       {
	   pw.print("\tnotification_failure_options\t");
	   int x = 0;
	   x += option( pw, temp_hostdependency.fail_on_up , "o", x );
	   x += option( pw, temp_hostdependency.fail_on_down, "d", x);
	   x += option( pw, temp_hostdependency.fail_on_unreachable, "u", x );
	   x += option( pw, temp_hostdependency.fail_on_pending, "p", x );
	   end_options( pw, x );
       }
       
       if (temp_hostdependency.dependency_type == common_h.EXECUTION_DEPENDENCY)
       {
	   pw.print("\texecution_failure_options\t");
	   int x = 0;
	   x += option( pw, temp_hostdependency.fail_on_up , "o", x );
	   x += option( pw, temp_hostdependency.fail_on_down , "d", x );
	   x += option( pw, temp_hostdependency.fail_on_unreachable, "u", x );
	   x += option( pw, temp_hostdependency.fail_on_pending , "p", x );
	   end_options( pw, x );
       }
       
       end_define( pw );
   }
   public static void writeHostEscalation( PrintWriter pw, objects_h.hostescalation temp_hostescalation )
   {
      define( pw, "hostescalation");
      entry( pw, "host_name" , temp_hostescalation.host_name);
      entry( pw, "first_notification" , temp_hostescalation.first_notification);
      entry( pw, "last_notification" , temp_hostescalation.last_notification);
      entry( pw, "notification_interval" , temp_hostescalation.notification_interval);
      entry( pw, "escalation_period" , temp_hostescalation.escalation_period);

      pw.print("\tescalation_options\t");
      int x = 0;
      x += option( pw, temp_hostescalation.escalate_on_down, "d", x );
      x += option( pw, temp_hostescalation.escalate_on_unreachable, "u", x );
      x += option( pw, temp_hostescalation.escalate_on_recovery, "r", x );
      end_options(pw, x);

      list(pw, "contact_groups", temp_hostescalation.contact_groups);
      end_define( pw );
   }

   public static void writeHostExtInfo( PrintWriter pw, objects_h.hostextinfo temp_hostextinfo ) { 
       define( pw, "hostextinfo" );
       entry(pw, "host_name" , temp_hostextinfo.host_name);
       entry(pw, "icon_image" , temp_hostextinfo.icon_image);
       entry(pw, "icon_image_alt" , temp_hostextinfo.icon_image_alt);
       entry(pw, "vrml_image" , temp_hostextinfo.vrml_image);
       entry(pw, "statusmap_image" , temp_hostextinfo.statusmap_image);
       if (temp_hostextinfo.have_2d_coords == common_h.TRUE)
	   pw.println("\t2d_coords\t" + temp_hostextinfo.x_2d + "," + temp_hostextinfo.y_2d);
       if (temp_hostextinfo.have_3d_coords == common_h.TRUE)
	   pw.println("\t3d_coords\t" + temp_hostextinfo.x_3d + "," + temp_hostextinfo.y_3d + ","
		   + temp_hostextinfo.z_3d);
       entry( pw, "notes" , temp_hostextinfo.notes);
       entry( pw, "notes_url" , temp_hostextinfo.notes_url);
       entry( pw, "action_url" , temp_hostextinfo.action_url);
       end_define( pw );
   }
   
   public static void writeServiceExtInfo( PrintWriter pw, objects_h.serviceextinfo temp_serviceextinfo) {
       define( pw, "serviceextinfo");
       entry( pw, "host_name" , temp_serviceextinfo.host_name);
       entry( pw, "service_description" , temp_serviceextinfo.description);
       entry( pw, "icon_image" , temp_serviceextinfo.icon_image);
       entry( pw, "icon_image_alt" , temp_serviceextinfo.icon_image_alt);
       entry( pw, "notes" , temp_serviceextinfo.notes);
       entry( pw, "notes_url" , temp_serviceextinfo.notes_url);
       entry( pw, "action_url" , temp_serviceextinfo.action_url);
       end_define( pw );
   }
   
   public static void define( PrintWriter pw, String name ) { 
       pw.printf("define %s {\n", name );
   }
   
   public static void end_define( PrintWriter pw ) {
       pw.println("\t}");
       pw.println();
   }
   
   public static void entry(PrintWriter pw, String name, String value)
   {
	if (value != null && value.length() != 0 )
		pw.printf("\t%s\t%s\n",name,value);
   }

   public static void entry(PrintWriter pw, String name, int value) {
	   	pw.printf("\t%s\t%d\n",name,value);
   }

   public static void entry(PrintWriter pw, String name, double value) {
	    pw.printf("\t%s\t%f\n",name,value);
	    //pw.printf("\t%s\t%d\n" + name, value);
	    pw.printf("\t%s\t%f\n",name,value);
  }
   
   public static int option( PrintWriter pw, int test, String value, int x ) {
       return option( pw, ( test == common_h.TRUE ),value , x );
   }

   public static int option( PrintWriter pw, boolean  test, String value, int x ) { 
       if ( test ) { 
	   pw.print(((x++ > 0) ? "," : "") + value );
	   return 1;
       }
       
       return 0;
   }


   public static void end_options( PrintWriter pw, int x ) { 
       if (x == 0)
	   pw.print("n");
       pw.println();
   }
   
   public static void list(PrintWriter pw, String name, List list) {
	if (list != null && !list.isEmpty()) {
	    pw.print("\t" + name + "\t");
	    boolean first = true;
	    for (Object obj : list) {
		if (!first)
		    pw.print(",");
		else
		    first = false;
		pw.print(obj.toString());
	    }
	    pw.println();
	}
    }

}