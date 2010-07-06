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

package com.hironytic.moltonf.model.limited;

import java.util.List;

import com.hironytic.moltonf.MoltonfException;
import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.StoryElement;
import com.hironytic.moltonf.model.StoryPeriod;

/**
 * 経過日数や人物によって得られる情報を制限した StoryPeriod
 */
public class LimitedStoryPeriod implements StoryPeriod {

    /** このオブジェクトが属する LimitedStory */
    private LimitedStory story;
    
    /** 元となる StoryPeriod */
    private StoryPeriod basePeriod;
    
    /**
     * コンストラクタ
     * @param story このオブジェクトが属する LimitedStory
     * @param basePeriod 元となる StoryPeriod
     */
    public LimitedStoryPeriod(LimitedStory story, StoryPeriod basePeriod) {
        this.story = story;
        this.basePeriod = basePeriod;
    }
    
    /**
     * @see com.hironytic.moltonf.model.StoryPeriod#getStory()
     */
    @Override
    public Story getStory() {
        return story;
    }

    /* (non-Javadoc)
     * @see com.hironytic.moltonf.model.StoryPeriod#getStoryElements()
     */
    @Override
    public List<StoryElement> getStoryElements() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.hironytic.moltonf.model.StoryPeriod#setStory(com.hironytic.moltonf.model.Story)
     */
    @Override
    public void setStory(Story story) {
        throw new MoltonfException("Don't set story.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.StoryPeriod#setStoryElements(java.util.List)
     */
    @Override
    public void setStoryElements(List<StoryElement> storyElements) {
        throw new MoltonfException("Don't set story elements.", new UnsupportedOperationException("Modification is not allowed."));
    }
}
