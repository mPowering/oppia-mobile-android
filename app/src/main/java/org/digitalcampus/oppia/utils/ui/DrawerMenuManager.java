/*
 * This file is part of OppiaMobile - https://digital-campus.org/
 *
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.oppia.utils.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.digitalcampus.mobile.learning.R;
import org.digitalcampus.oppia.activity.AboutActivity;
import org.digitalcampus.oppia.activity.AppActivity;
import org.digitalcampus.oppia.activity.MonitorActivity;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.activity.ScorecardActivity;
import org.digitalcampus.oppia.activity.SearchActivity;
import org.digitalcampus.oppia.activity.TagSelectActivity;
import org.digitalcampus.oppia.application.AdminSecurityManager;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.application.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class DrawerMenuManager {

    public interface MenuOption{ void onOptionSelected(); }

    private AppActivity drawerAct;
    private boolean isRootActivity = false;

    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Map<Integer, MenuOption> customOptions = new HashMap<>();

    public DrawerMenuManager(AppActivity act, boolean isRootActivity){
        drawerAct = act;
        this.isRootActivity = isRootActivity;
    }

    public void initializeDrawer(){
        // Initializing Drawer Layout and ActionBarToggle
        final Toolbar toolbar = (Toolbar) drawerAct.findViewById(R.id.toolbar);
        final DrawerLayout drawerLayout = (DrawerLayout) drawerAct.findViewById(R.id.drawer);
        navigationView = (NavigationView) drawerAct.findViewById(R.id.navigation_view);

        if (drawerLayout == null || navigationView == null) return;
        View headerView = navigationView.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.drawer_user_fullname)).setText(
                SessionManager.getUserDisplayName(drawerAct));
        ((TextView) headerView.findViewById(R.id.drawer_username)).setText(
                SessionManager.getUsername(drawerAct));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                boolean result = onOptionsItemSelected(menuItem);
                if (result){
                    menuItem.setChecked(false);
                    drawerLayout.closeDrawers();
                }
                return result;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(drawerAct,drawerLayout,toolbar,R.string.open, R.string.close);
        //Setting the actionbarToggle to drawer layout
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    public void onPrepareOptionsMenu(Menu menu, int currentOption){
        this.onPrepareOptionsMenu(menu, currentOption, null);
    }
    public void onPrepareOptionsMenu(Menu menu, Map<Integer, MenuOption> options){
        this.onPrepareOptionsMenu(menu, null, options);
    }
    public void onPrepareOptionsMenu(Menu menu, Integer currentOption, Map<Integer, MenuOption> options){

        if (options != null)
            this.customOptions = options;

        Menu drawerMenu = navigationView.getMenu();
        MenuItem itemLogout = drawerMenu.findItem(R.id.menu_logout);
        MenuItem itemSettings = drawerMenu.findItem(R.id.menu_settings);
        MenuItem itemMonitor = drawerMenu.findItem(R.id.menu_monitor);
        MenuItem itemCourseDownload = drawerMenu.findItem(R.id.menu_download);
        MenuItem itemLanguageDialog = drawerMenu.findItem(R.id.menu_language);

        if (currentOption != null){
            MenuItem current = drawerMenu.findItem(currentOption);
            if (current != null){
                current.setCheckable(true);
                current.setChecked(true);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(drawerAct);
        itemLogout.setVisible(prefs.getBoolean(PrefsActivity.PREF_LOGOUT_ENABLED, MobileLearning.MENU_ALLOW_LOGOUT));
        itemSettings.setVisible(MobileLearning.MENU_ALLOW_SETTINGS);
        itemMonitor.setVisible(MobileLearning.MENU_ALLOW_MONITOR);
        itemCourseDownload.setVisible(MobileLearning.MENU_ALLOW_COURSE_DOWNLOAD);
        itemLanguageDialog.setVisible(customOptions.containsKey(R.id.menu_language));
    }

    public void onPostCreate(Bundle savedInstanceState){
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig){
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private boolean onOptionsItemSelected(MenuItem item){
        // If it is the current selected item, we do nothing
        if (item.isChecked()) return false;

        final int itemId = item.getItemId();
        AdminSecurityManager.checkAdminPermission(drawerAct, itemId, new AdminSecurityManager.AuthListener() {
            public void onPermissionGranted() {
                // Check if the option has custom manager
                if(
                    customOptions.containsKey(itemId)){
                    customOptions.get(itemId).onOptionSelected();
                }
                else if (itemId == R.id.menu_download) {
                    launchIntentForActivity(TagSelectActivity.class);
                } else if (itemId == R.id.menu_about) {
                    launchIntentForActivity(AboutActivity.class);
                } else if (itemId == R.id.menu_monitor) {
                    launchIntentForActivity(MonitorActivity.class);
                } else if (itemId == R.id.menu_scorecard) {
                    launchIntentForActivity(ScorecardActivity.class);
                } else if (itemId == R.id.menu_search) {
                    launchIntentForActivity(SearchActivity.class);
                } else if (itemId == R.id.menu_settings) {
                    launchIntentForActivity(PrefsActivity.class);
                }else if (itemId == R.id.menu_logout) {
                    logout();
                }
            }
        });

        return true;
    }

    public void launchIntentForActivity(Class<?> activityClass){
        Intent i = new Intent(drawerAct, activityClass);
        if (!this.isRootActivity){
            //If the activity was not the root one, we close it
            drawerAct.finish();
        }
        drawerAct.overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.fade_out);
        drawerAct.startActivity(i);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(drawerAct, R.style.Oppia_AlertDialogStyle);
        builder.setCancelable(false);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_confirm);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                drawerAct.logoutAndRestartApp();
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
    }

}
