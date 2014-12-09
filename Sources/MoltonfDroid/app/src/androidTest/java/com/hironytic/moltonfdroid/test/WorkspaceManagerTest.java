package com.hironytic.moltonfdroid.test;

import java.io.File;

import com.hironytic.moltonfdroid.MoltonfException;
import com.hironytic.moltonfdroid.model.Workspace;
import com.hironytic.moltonfdroid.model.WorkspaceManager;

import android.database.Cursor;
import android.test.AndroidTestCase;

public class WorkspaceManagerTest extends AndroidTestCase {

    private WorkspaceManager wsManager;
    
    public WorkspaceManagerTest() {
    }

    /**
     * @see android.test.AndroidTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        wsManager = new WorkspaceManager(this.getContext());
        wsManager.deleteAll();
    }

    /**
     * @see android.test.AndroidTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        wsManager.close();
        wsManager = null;
        super.tearDown();
    }

    public void testSaveAndLoad() {
        // ワークスペースを保存
        String title = "this is a title.";
        File packageDir = new File("/path/dir");
        
        Workspace ws = new Workspace();
        ws.setTitle(title);
        ws.setPackageDir(packageDir);
        
        wsManager.save(ws);
        
        long wsId = ws.getWorkspaceId();
        assertTrue(wsId > 0);
        
        // ワークスペースを読み込み
        Workspace ws2 = wsManager.load(wsId);
        
        assertEquals(wsId, ws2.getWorkspaceId());
        assertEquals(title, ws2.getTitle());
        assertEquals(packageDir, ws2.getPackageDir());
    }
    
    public void testList() {
        String title1 = "First Title";
        File packageDir1 = new File("/path/first");
        Workspace ws1 = new Workspace();
        ws1.setTitle(title1);
        ws1.setPackageDir(packageDir1);
        wsManager.save(ws1);

        String title2 = "Second Title";
        File packageDir2 = new File("/path/second");
        Workspace ws2 = new Workspace();
        ws2.setTitle(title2);
        ws2.setPackageDir(packageDir2);
        wsManager.save(ws2);

        String title3 = "Third Title";
        File packageDir3 = new File("/path/third");
        Workspace ws3 = new Workspace();
        ws3.setTitle(title3);
        ws3.setPackageDir(packageDir3);
        wsManager.save(ws3);
        
        Cursor cursor = wsManager.list();
        assertNotNull(cursor);
        try {
            boolean hasRow;
            String title;
            long id;
            long updated, prevUpdated;
            
            // 更新日時の新しい順に並ぶ仕様なので、title3, title2, title1 の順になっているはず
            hasRow = cursor.moveToFirst();
            assertTrue(hasRow);
            id = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_ID);
            title = cursor.getString(WorkspaceManager.LIST_COLUMN_INDEX_TITLE);
            updated = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_UPDATED);
            assertEquals(ws3.getWorkspaceId(), id);
            assertEquals(ws3.getTitle(), title);
            prevUpdated = updated;
            
            hasRow = cursor.moveToNext();
            assertTrue(hasRow);
            id = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_ID);
            title = cursor.getString(WorkspaceManager.LIST_COLUMN_INDEX_TITLE);
            updated = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_UPDATED);
            assertEquals(ws2.getWorkspaceId(), id);
            assertEquals(ws2.getTitle(), title);
            assertTrue(updated <= prevUpdated);
            prevUpdated = updated;
            
            hasRow = cursor.moveToNext();
            assertTrue(hasRow);
            id = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_ID);
            title = cursor.getString(WorkspaceManager.LIST_COLUMN_INDEX_TITLE);
            updated = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_UPDATED);
            assertEquals(ws1.getWorkspaceId(), id);
            assertEquals(ws1.getTitle(), title);
            assertTrue(updated <= prevUpdated);
            prevUpdated = updated;
            
            hasRow = cursor.moveToNext();
            assertFalse(hasRow);
        } finally {
            cursor.close();
        }
    }
    
    public void testDelete() {
        String title = "title to delete";
        File packageDir = new File("/path/del");
        Workspace ws = new Workspace();
        ws.setTitle(title);
        ws.setPackageDir(packageDir);
        wsManager.save(ws);
        long wsId = ws.getWorkspaceId();

        wsManager.delete(wsId);
        
        // 削除したものは読み込めない
        try {
            wsManager.load(wsId);
            fail("expected: MoltonfException is thrown");
        } catch (MoltonfException ex) {
        }
        
        // 削除したので一覧には何もない
        Cursor cursor = wsManager.list();
        assertNotNull(cursor);
        try {
            assertFalse(cursor.moveToFirst());
        } finally {
            cursor.close();
        }
    }

    public void testUpdate() {
        String titlePre = "previous title";
        File packageDirPre = new File("/path/pre");
        
        Workspace ws = new Workspace();
        ws.setTitle(titlePre);
        ws.setPackageDir(packageDirPre);
        
        wsManager.save(ws);
        long wsId = ws.getWorkspaceId();
        
        String titleNew = "new title";
        File packageDirNew = new File("/path/new");

        ws.setTitle(titleNew);
        ws.setPackageDir(packageDirNew);
        
        wsManager.save(ws);
        assertEquals(wsId, ws.getWorkspaceId());
        
        Workspace ws2 = wsManager.load(wsId);
        assertEquals(titleNew, ws2.getTitle());
        assertEquals(packageDirNew, ws2.getPackageDir());
        
        Cursor cursor = wsManager.list();
        assertNotNull(cursor);
        try {
            assertTrue(cursor.moveToFirst());
            
            assertEquals(wsId, cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_ID));
            assertEquals(titleNew, cursor.getString(WorkspaceManager.LIST_COLUMN_INDEX_TITLE));
        } finally {
            cursor.close();
        }
    }
}
