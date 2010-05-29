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

package com.hironytic.moltonf.resource;

import java.util.ListResourceBundle;

/**
 * Moltonf のローカライズ向けリソース。
 * 
 * 今のところ Moltonf のローカライズ対応は考えていません。
 * 将来、対応しようと思ったときのために ResourceBundle を
 * 使うようにだけする目的でこのクラスを用意しています。
 */
public class Resources extends ListResourceBundle {

    /**
     * @see java.util.ListResourceBundle#getContents()
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            // アプリケーション全体
            {"app.title", "Moltonf"},
            {"workspaceFilter.title", "観戦データ"},
            
            // メインフレーム メニュー
            {"mainFrame.menu.file", "ファイル(F)"},
            {"mainFrame.menu.help", "ヘルプ(H)"},
            
            // メインフレーム コマンド
            {"mainFrame.command.newWorkspace", "新しい観戦(N)..."},
            {"mainFrame.command.openWorkspace", "観戦データを開く(O)..."},
            {"mainFrame.command.exit", "終了(X)"},
            {"mainFrame.command.about", "バージョン情報(A)"},
            
            // ピリオドビュー
            {"periodView.nextDay", "次の日へ"},
            
            // ダイアログ共通
            {"dialog.buttonOk", "OK"},
            {"dialog.buttonCancel", "キャンセル"},
            
            // 新規ワークスペース作成ダイアログ
            {"newWorkspaceDialog.title", "新しい観戦データの作成"},
            {"newWorkspaceDialog.browse", "参照(B)"},
            {"newWorkspaceDialog.playData", "プレイデータ"},
            {"newWorkspaceDialog.playData.eyeCatch", "観戦するプレイデータのアーカイブファイルを指定してください。"},
            {"newWorkspaceDialog.browse.title", "プレイデータ アーカイブの選択"},
            {"newWorkspaceDialog.browse.btnApprove", "選択"},
            {"newWorkspaceDialog.browser.xmlFilter", "XML ファイル"},
            {"newWorkspaceDialog.warning.cannotReadPlayData", "指定されたアーカイブファイルが見つからないか、\n読み込むことができません。"},
            
            // ワークスペースファイル選択ダイアログ
            {"workspaceSaveDialog.title", "観戦データの保存先"},
            {"workspaceOpenDialog.title", "観戦データを開く"},
            
            // フィルター サイドバー
            {"filterSideBar.speakerFilter", "人物"},
            {"filterSideBar.talkTypeFilter", "発言種別"},
            {"filterSideBar.talkTypeFilter.public", "通常発言"},
            {"filterSideBar.talkTypeFilter.wolf", "狼のささやき"},
            {"filterSideBar.talkTypeFilter.private", "独り言"},
            {"filterSideBar.talkTypeFilter.grave", "墓下発言"},
            {"filterSideBar.eventFamilyFilter", "イベント種別"},
            {"filterSideBar.eventFamilyFilter.announce", "アナウンス"},
            {"filterSideBar.eventFamilyFilter.extra", "特殊能力系"},
            {"filterSideBar.eventFamilyFilter.order", "操作系"},
            
        };
    }

}
