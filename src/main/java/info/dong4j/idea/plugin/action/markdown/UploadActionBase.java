/*
 * MIT License
 *
 * Copyright (c) 2019 dong4j <dong4j@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package info.dong4j.idea.plugin.action.markdown;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;

import info.dong4j.idea.plugin.client.OssClient;
import info.dong4j.idea.plugin.content.MarkdownContents;
import info.dong4j.idea.plugin.entity.MarkdownImage;
import info.dong4j.idea.plugin.notify.UploadNotification;
import info.dong4j.idea.plugin.strategy.UploadFromAction;
import info.dong4j.idea.plugin.strategy.Uploader;
import info.dong4j.idea.plugin.util.ActionUtils;
import info.dong4j.idea.plugin.util.MarkdownUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Company: 科大讯飞股份有限公司-四川分公司</p>
 * <p>Description: 右键上传到 OSS </p>
 *
 * @author dong4j
 * @date 2019 -03-14 17:15
 * @email sjdong3 @iflytek.com
 */
@Slf4j
public abstract class UploadActionBase extends AnAction {
    /**
     * Gets icon.
     *
     * @return the icon
     */
    abstract protected Icon getIcon();

    /**
     * action 是否为可用状态
     *
     * @return the boolean
     */
    abstract boolean isAvailable();

    /**
     * 获取 action name
     *
     * @return the name
     */
    abstract String getName();

    /**
     * 检查 "upload to XXX OSS" 按钮是否可用
     * 1. 相关 test 通过后
     * a. 如果全是目录则可用
     * b. 如果文件是 markdown 才可用
     *
     * @param event the event
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        ActionUtils.isAvailable(event, getIcon(), MarkdownContents.MARKDOWN_TYPE_NAME);
    }

    /**
     * 所有子类都走这个逻辑, 做一些前置判断和解析 markdown image mark
     *
     * @param event the an action event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        final Project project = event.getProject();
        if (project != null) {
            // 如果账号未设置或者设置后未测试, 则给出提示
            if (!isAvailable()) {
                log.trace("No account set or setting error. action = {}", getName());
                UploadNotification.notifyConfigurableError(project, getName());
                return;
            }

            Map<Document, List<MarkdownImage>> waitingForUploadImages = MarkdownUtils.getProcessMarkdownInfo(event,
                                                                                                             project);

            execute(event, waitingForUploadImages);
        }
    }

    /**
     * 新建后台任务, 调用 upload() 执行上传逻辑
     *
     * @param event                  the event
     * @param waitingForUploadImages the waiting for upload images
     * @return the string   url      上传成功后返回的 url
     */
    @Contract(pure = true)
    private void execute(@NotNull AnActionEvent event, Map<Document, List<MarkdownImage>> waitingForUploadImages) {
        if (waitingForUploadImages.size() > 0) {
            // 先刷新一次, 避免才添加的文件未被添加的 VFS 中, 导致找不到文件的问题
            VirtualFileManager.getInstance().syncRefresh();
            // 获取对应的 client
            OssClient ossClient = getOssClient();
            Uploader.getInstance()
                .setUploadWay(new UploadFromAction(event.getProject(), ossClient, waitingForUploadImages))
                .upload();
        }
    }

    /**
     * 获取具体上传的客户端, 委托给后台任务执行
     *
     * @return the oss client
     */
    abstract OssClient getOssClient();
}