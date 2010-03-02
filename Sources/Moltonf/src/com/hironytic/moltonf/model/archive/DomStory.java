/*
 * Moltonf
 *
 * Copyright (c) 2010 Project Moltonf
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

package com.hironytic.moltonf.model.archive;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.StoryPeriod;
import com.hironytic.moltonf.model.VillageState;
import com.hironytic.moltonf.util.DomUtils;

/**
 * 共通アーカイブ基盤用スキーマにおける village 要素による Story 実装
 */
public class DomStory implements Story {

    /** DOMツリー上で、このオブジェクトが対象とする village 要素 */
    private Element villageElem;

    /**
     * コンストラクタ
     * @param villageElem village 要素
     */
    public DomStory(Element villageElem) {
        this.villageElem = villageElem;
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getAvatarList()
     */
    @Override
    public List<Avatar> getAvatarList() {
        List<Avatar> list = new ArrayList<Avatar>();
        Node avatarListElem = DomUtils.searchSiblingForward(villageElem.getFirstChild(),
                SchemaConstants.NS_TNS, SchemaConstants.LN_AVATAR_LIST);
        if (avatarListElem != null) {
            Node avatarElem = DomUtils.searchSiblingForward(avatarListElem.getFirstChild(),
                    SchemaConstants.NS_TNS, SchemaConstants.LN_AVATAR);
            while (avatarElem != null) {
                list.add(new DomAvatar((Element)avatarElem));
                avatarElem = DomUtils.searchSiblingForward(avatarElem.getNextSibling(),
                        SchemaConstants.NS_TNS, SchemaConstants.LN_AVATAR);
            }
        }
        return list;
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getBaseUri()
     */
    @Override
    public URI getBaseUri() {
        URI baseUri = null;
        String baseUriString = villageElem.getAttributeNS(SchemaConstants.NS_XML, SchemaConstants.LN_BASE);
        if (!baseUriString.isEmpty()) {
            try {
                baseUri = new URI(baseUriString);
            } catch (URISyntaxException ex) {
                baseUri = null;
            }
        }
        return baseUri;
    }

    /* (non-Javadoc)
     * @see com.hironytic.moltonf.model.Story#getPeriods()
     */
    @Override
    public List<StoryPeriod> getPeriods() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getVillageFullName()
     */
    @Override
    public String getVillageFullName() {
        return villageElem.getAttributeNS(null, SchemaConstants.LN_FORMAL_NAME);
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getVillageState()
     */
    @Override
    public VillageState getVillageState() {
        String villageStateString = villageElem.getAttributeNS(null, SchemaConstants.LN_STATE);
        if (SchemaConstants.VAL_VILLAGE_STATE_GAMEOVER.equals(villageStateString)) {
            return VillageState.GAMEOVER;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_EPILOGUE.equals(villageStateString)) {
            return VillageState.EPILOGUE;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_PROGRESS.equals(villageStateString)) {
            return VillageState.PROGRESS;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_PROLOGUE.equals(villageStateString)) {
            return VillageState.PROLOGUE;
        } else {
            return null;
        }
    }

}
