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

package org.digitalcampus.oppia.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.opendeliver.oppia.R;
import org.digitalcampus.oppia.activity.CourseIndexActivity;
import org.digitalcampus.oppia.activity.TagSelectActivity;
import org.digitalcampus.oppia.adapter.ScorecardListAdapter;
import org.digitalcampus.oppia.application.AdminSecurityManager;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.model.CoursesRepository;

import java.util.ArrayList;

import javax.inject.Inject;

public class GlobalScorecardFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = CourseScorecardFragment.class.getSimpleName();
    private ScorecardListAdapter scorecardListAdapter;

    @Inject
    CoursesRepository coursesRepository;

    public static GlobalScorecardFragment newInstance() {
        return new GlobalScorecardFragment();
    }
    public GlobalScorecardFragment(){ }

    private void initializeDagger() {
        MobileLearning app = (MobileLearning) getActivity().getApplication();
        app.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vv = super.getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_scorecards, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        vv.setLayoutParams(lp);
        return vv;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeDagger();


        ArrayList<Course> courses = coursesRepository.getCourses(getActivity());

        GridView scorecardList = (GridView) super.getActivity().findViewById(R.id.scorecards_list);
        View emptyState = this.getActivity().findViewById(R.id.empty_state);

        if (courses.size() == 0){
            //If there are now courses, display the empty state
            scorecardList.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);

            Button download = (Button) emptyState.findViewById(R.id.btn_download_courses);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminSecurityManager.checkAdminPermission(getActivity(), R.id.menu_download, new AdminSecurityManager.AuthListener() {
                        public void onPermissionGranted() {
                            startActivity(new Intent(getActivity(), TagSelectActivity.class));
                        }
                    });
                }
            });
        }
        else{
            scorecardList.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            scorecardListAdapter = new ScorecardListAdapter(super.getActivity(), courses);
            scorecardList.setAdapter(scorecardListAdapter);
            scorecardList.setOnItemClickListener(this);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Course selectedCourse = scorecardListAdapter.getItem(position);
        Intent i = new Intent(super.getActivity(), CourseIndexActivity.class);
        Bundle tb = new Bundle();
        tb.putSerializable(Course.TAG, selectedCourse);
        i.putExtras(tb);
        startActivity(i);
        super.getActivity().finish();
    }
}
