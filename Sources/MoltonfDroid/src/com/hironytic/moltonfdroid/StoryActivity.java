/*
 * Moltonf
 *
 * Copyright (c) 2014 Hironori Ichimiya <hiron@hironytic.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hironytic.moltonfdroid;

import com.hironytic.moltonfdroid.model.Workspace;
import com.hironytic.moltonfdroid.model.WorkspaceManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 
 */
public class StoryActivity extends FragmentActivity {

    /** 表示するワークスペースのID */
    public static final String EXTRA_KEY_WORKSPACE_ID = "Moltonf.WorkspaceID";
    
    /**
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState == null) {
            StoryFragment fragment = new StoryFragment();
            
            // 読み込むストーリーの受け渡し
            Intent intent = getIntent();
            if (intent != null) {
                long workspaceId = intent.getLongExtra(EXTRA_KEY_WORKSPACE_ID, 0);
                if (workspaceId > 0) {
                    WorkspaceManager wsManager = new WorkspaceManager(getApplicationContext());
                    try {
                        Workspace workspace = wsManager.load(workspaceId);
                        fragment.setWorkspace(workspace);
                    } finally {
                        wsManager.close();
                    }
                }
            }
            
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
        }
    }
    
}
