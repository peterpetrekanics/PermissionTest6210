package com.test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.LayoutServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.PortletDisplay;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Portlet implementation class PermissionTest6210
 */
public class PermissionTest6210 extends MVCPortlet {
 
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		System.out.println("* ProcessAction starts.. *");
		
		
		
		System.out.println("* ProcessAction ends.. *");
	}
	
	@Override
	public void render(RenderRequest arg0, RenderResponse arg1)
			throws PortletException, IOException {

		// This code was created to ensure that only those users can access this portlet on their page,
		// who are added to the "CustomPortletViewerRole" role.
		System.out.println("-------  The portlet will now start checking the user's view permissions -------");
		
		// The program will now try to find out the portlet id of our portlet 
		ThemeDisplay themeDisplay = (ThemeDisplay) arg0
                .getAttribute(WebKeys.THEME_DISPLAY);
        PortletDisplay portletDisplay= themeDisplay.getPortletDisplay();
        String portletId= portletDisplay.getId();
        System.out.println("The portletId of this portlet is: "+portletId);
        
        // It will now find out the companyId and the current user's userId
		final long companyId = PortalUtil.getDefaultCompanyId();
		long currentUsersId = 0;
		try {
			currentUsersId = PortalUtil.getUser(arg0).getUserId();
			System.out.println("Current user's screenname is: " + PortalUtil.getUser(arg0).getScreenName());
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}

        // It will now find out the viewer role's ID
        long myPortletViewerRoleId = 0;
        String myPortletViewerRoleName = "CustomPortletViewerRole";
        Role myPortletViewerRole = null;
		try {
			myPortletViewerRole = RoleLocalServiceUtil.getRole(companyId, myPortletViewerRoleName);
		} catch (PortalException e1) {
			//e1.printStackTrace();
		} catch (SystemException e1) {
			//e1.printStackTrace();
		}
		if(myPortletViewerRole!=null){
			myPortletViewerRoleId = myPortletViewerRole.getRoleId();
		} else System.out.println("Please create the role: 'CustomPortletViewerRole' for the users who should have access to this portlet");		
        
		// Now it will check whether the user has the necessary role to view this portlet
        boolean userHasRole = false;
		try {
			userHasRole = RoleLocalServiceUtil.hasUserRole(currentUsersId, myPortletViewerRoleId);
//			System.out.println("userhasrole: " + userHasRole);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		        
        // If the user has no view permissions, the portlet will be deleted from the page
		Layout userLayout = themeDisplay.getLayout();
		if(!userHasRole){
			System.out.println(" * Result of permission checking: *");
			System.out.println("The current user does NOT have the necessary role to access this portlet");
			System.out.println("Starting to delete the portlet from the page");
			
			LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet)userLayout.getLayoutType();
			layoutTypePortlet.removePortletId(currentUsersId, portletId);
			
			System.out.println("Portlet deletion ended");
		} else {
			System.out.println("* Result of permission checking: *");
			System.out.println("The current user HAS the necessary role to access this portlet");
			super.render(arg0, arg1);
		}
		try {
			LayoutServiceUtil.updateLayout(userLayout.getGroupId(), userLayout.getPrivateLayout(),userLayout.getLayoutId(), userLayout.getTypeSettings());
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
        // CODE TO HIDE PORTLET:
//		arg0.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.FALSE);
//		arg0.setAttribute(WebKeys.PORTLET_DECORATE, Boolean.FALSE);
//		Object myAttrib = arg0.getAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY);
//		System.out.println(myAttrib);
//
//		Boolean portletConfiguratorVisibility =
//				                 (Boolean)arg0.getAttribute(
//				                     WebKeys.PORTLET_CONFIGURATOR_VISIBILITY);

		System.out.println("-------  View permission checking ended -------");
	}
}
