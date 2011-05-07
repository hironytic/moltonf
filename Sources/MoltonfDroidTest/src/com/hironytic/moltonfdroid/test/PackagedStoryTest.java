package com.hironytic.moltonfdroid.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.VillageState;
import com.hironytic.moltonfdroid.model.archived.PackagedStory;

import android.content.res.AssetManager;
import android.os.Environment;
import android.test.InstrumentationTestCase;

public class PackagedStoryTest extends InstrumentationTestCase {

    private PackagedStory story;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        File testDataDir = getTestDataDir();
        AssetManager assetManager = getInstrumentation().getContext().getAssets();
        readyTestData(assetManager, testDataDir, "wolff_00000/village.xml", "village.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-0.xml", "period-0.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-1.xml", "period-1.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-2.xml", "period-2.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-3.xml", "period-3.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-4.xml", "period-4.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-5.xml", "period-5.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-6.xml", "period-6.xml");
        readyTestData(assetManager, testDataDir, "wolff_00000/period-7.xml", "period-7.xml");
        
        story = new PackagedStory(testDataDir);
    }

    /**
     * @see android.test.InstrumentationTestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * テストデータを格納するディレクトリを求めます。
     * @return
     */
    private File getTestDataDir() {
        try {
            File sdcardDir = Environment.getExternalStorageDirectory();
            File rootDir = new File(sdcardDir, "MoltonfDroidPackagedStoryTest");
            if (!rootDir.exists()) {
                if (sdcardDir.canWrite()) {
                    rootDir.mkdir();
                } else {
                    return null;
                }
            }
            File testDataDir = new File(rootDir, "wolff_00000");
            if (!testDataDir.exists()) {
                if (rootDir.canWrite()) {
                    testDataDir.mkdir();
                } else {
                    return null;
                }
            }
            return testDataDir;
        } catch (SecurityException ex) {
            return null;
        }
    }
    
    /**
     * テストデータを準備します。
     * @param assetManager
     * @param testDataDir
     * @param assetFileName
     * @param testDataFileName
     * @throws Exception
     */
    private void readyTestData(AssetManager assetManager, File testDataDir, String assetFileName, String testDataFileName) throws Exception {
        File testDataFile = new File(testDataDir, testDataFileName);
        if (!testDataFile.exists()) {
            InputStream assetStream = assetManager.open(assetFileName);
            try {
                final int BUFFER_SIZE = 4096;
                byte[] data = new byte[BUFFER_SIZE];
                OutputStream testDataStream = new FileOutputStream(testDataFile);
                try {
                    int readSize = assetStream.read(data);
                    while (readSize > 0) {
                        testDataStream.write(data, 0, readSize);
                        readSize = assetStream.read(data);
                    }
                } finally {
                    testDataStream.close();
                }
            } finally {
                assetStream.close();
            }
        }
    }
    
    public void testReadiness() {
        assertFalse(story.isReady());
        
        story.ready();
        
        assertTrue(story.isReady());
    }
    
    public void testPeriodCount() {
        story.ready();
        
        assertEquals(8, story.getPeriodCount());
    }
    
    public void testPeriod() {
        story.ready();
        
        StoryPeriod period2 = story.getPeriod(2);
        assertNotNull(period2);
        assertEquals(story, period2.getStory());
    }
    
    public void testAvatarList() throws URISyntaxException {
        story.ready();
        
        List<Avatar> avatarList = story.getAvatarList();
        assertNotNull(avatarList);
        
        assertEquals(16, avatarList.size());
        
        Avatar avatar0 = avatarList.get(0);
        assertEquals(story, avatar0.getStory());
        assertEquals("gerd", avatar0.getAvatarId());
        assertEquals("楽天家 ゲルト", avatar0.getFullName());
        assertEquals("ゲルト", avatar0.getShortName());
        assertEquals(new URI("http://192.168.150.129/moltonf/plugin_wolf/img/face01.jpg"), avatar0.getFaceIconUri());
        
        Avatar avatar3 = avatarList.get(3);
        assertEquals(story, avatar3.getStory());
        assertEquals("liesa", avatar3.getAvatarId());
        assertEquals("少女 リーザ", avatar3.getFullName());
        assertEquals("リーザ", avatar3.getShortName());
        assertEquals(new URI("http://192.168.150.129/moltonf/plugin_wolf/img/face09.jpg"), avatar3.getFaceIconUri());
    }
    
    public void testAvatar() throws URISyntaxException {
        story.ready();
        
        Avatar avatar = story.getAvatar("dieter");
        assertNotNull(avatar);
        assertEquals(story, avatar.getStory());
        assertEquals("dieter", avatar.getAvatarId());
        assertEquals("ならず者 ディーター", avatar.getFullName());
        assertEquals("ディーター", avatar.getShortName());
        assertEquals(new URI("http://192.168.150.129/moltonf/plugin_wolf/img/face07.jpg"), avatar.getFaceIconUri());
        
        Avatar notExistAvatar = story.getAvatar("walter");
        assertNull(notExistAvatar);
    }
    
    public void testVillageState() throws URISyntaxException {
        story.ready();
        
        assertEquals("F0 テスト用の村", story.getVillageFullName());
        assertEquals(VillageState.GAMEOVER, story.getVillageState());
        assertEquals(new URI("http://192.168.150.129/moltonf/plugin_wolf/img/face99.jpg"), story.getGraveIconUri());
    }
}
