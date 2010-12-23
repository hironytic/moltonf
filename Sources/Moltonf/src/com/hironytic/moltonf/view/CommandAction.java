/*
 * Moltonf
 *
 * Copyright (c) 2010 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonf.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.EventListenerList;

import com.hironytic.moltonf.Moltonf;

/**
 * Moltonf ビューで発行されるコマンドの Action
 */
@SuppressWarnings("serial")
public class CommandAction extends AbstractAction {

    /** コマンド実行のリスナーリスト */
    private final EventListenerList commandListenerList = new EventListenerList();
    
    /**
     * コンストラクタ
     * @param actionCommand コマンド文字列
     * @param mnemonic ニーモニック
     */
    public CommandAction(String actionCommand, int mnemonic) {
        String text = Moltonf.getResource().getString(actionCommand);
        putValue(Action.ACTION_COMMAND_KEY, actionCommand);
        putValue(Action.NAME, text);
        putValue(Action.MNEMONIC_KEY, mnemonic);
    }
    
    /**
     * コマンド用のアクションリスナーを追加します。
     * @param l
     */
    public void addCommandListener(ActionListener l) {
        commandListenerList.add(ActionListener.class, l);
    }

    /**
     * コマンド用のアクションリスナーを削除します。
     * @param l
     */
    public void removeCommandListener(ActionListener l) {
        commandListenerList.remove(ActionListener.class, l);
    }

    /**
     * コマンドを実行します。
     * @param source コマンドを実行するイベントの発生元
     */
    public void fireCommand(Object source) {
        // Guaranteed to return a non-null array
        Object[] listeners = commandListenerList.getListenerList();
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new ActionEvent(source,
                                        ActionEvent.ACTION_PERFORMED,
                                        getActionCommand());
                }
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }          
        }
    }
    
    /**
     * コマンドを実行します。
     * @param e ActionEvent
     */
    private void fireCommand(ActionEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = commandListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }
        }
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        fireCommand(e);
    }
    
    /**
     * このコマンドのコマンド文字列を返します。
     * @return コマンド文字列
     */
    public String getActionCommand() {
        return (String)getValue(Action.ACTION_COMMAND_KEY);
    }
}
