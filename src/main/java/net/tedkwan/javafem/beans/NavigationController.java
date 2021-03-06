
package net.tedkwan.javafem.beans;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;

/**
 * Managed Bean for some basic JSF navigation.
 *
 *
 * This managed bean is the bean which allows for some of the navigation.
 *
 * @author Ted Kwan
 */
@Named(value = "navigationController")
@SessionScoped
public class NavigationController  implements Serializable{
    /**
     * Creates a new instance of NavigationController
     */
    public NavigationController() {
    }
   @ManagedProperty(value="#{param.pageId}")
   private String pageId;

   /**
    * condional navigation based on pageId
    * 
    * if pageId is 1 show page1.xhtml,
    * if pageId is 2 show page2.xhtml
    * else show home.xhtml
    * 
    * @return string for the page to be used in faces-config.xml
    */
   public String showPage(){
      if(pageId == null){
         return "home";
      }
        switch (pageId) {
            case "1":
                System.out.println("home");
                return "home";
            case "2":
                System.out.println("pictures");
                return "pictures";
            default:
                return "home";
        }
   }
    
}
