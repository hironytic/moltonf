package com.hironytic.moltonfdroid.test;

import java.io.File;

import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.Workspace;

import android.test.AndroidTestCase;

public class WorkspaceTest extends AndroidTestCase {

    public WorkspaceTest() {
        super();
    }

    public void testNewWorkspace() {
        Workspace ws = new Workspace();

        assertEquals(false, ws.isModified());
    }

    public void testWorkspaceId() {
        Workspace ws = new Workspace();
        int id = 100;
        ws.setWorkspaceId(id);
        assertEquals(true, ws.isModified());
        assertEquals(id, ws.getWorkspaceId());
    }

    public void testPackageDir() {
        Workspace ws = new Workspace();
        File packageDir = new File("/path/dir");
        ws.setPackageDir(packageDir);
        assertEquals(packageDir, ws.getPackageDir());

        Story story = ws.getStory();
        assertNotNull(story);

        assertEquals(true, ws.isModified());
    }

    public void testTitle() {
        Workspace ws = new Workspace();
        String title = "Workspaceのテスト";
        ws.setTitle(title);
        assertEquals(title, ws.getTitle());

        assertEquals(true, ws.isModified());
    }
}
