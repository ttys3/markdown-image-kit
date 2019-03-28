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

package info.dong4j.idea.plugin.action.image;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import info.dong4j.idea.plugin.content.ImageContents;
import info.dong4j.idea.plugin.settings.MikPersistenComponent;
import info.dong4j.idea.plugin.settings.MikState;
import info.dong4j.idea.plugin.util.ActionUtils;
import info.dong4j.idea.plugin.util.ImageUtils;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Company: 科大讯飞股份有限公司-四川分公司</p>
 * <p>Description: 图片 压缩, 直接上传后将 url 写入到 clipboard </p>
 *
 * @author dong4j
 * @email sjdong3 @iflytek.com
 * @since 2019 -03-26 15:32
 */
@Slf4j
public abstract class ImageActionBase extends AnAction {
    protected static final MikState STATE = MikPersistenComponent.getInstance().getState();

    /**
     * Gets icon.
     *
     * @return the icon
     */
    abstract protected Icon getIcon();

    @Override
    public void update(@NotNull AnActionEvent event) {
        ActionUtils.isAvailable(event, getIcon(), ImageContents.IMAGE_TYPE_NAME);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Map<String, File> imageMap = new HashMap<>(32);
        Map<String, VirtualFile> virtualFileMap = new HashMap<>(32);

        final Project project = event.getProject();
        if (project != null) {

            log.trace("project's base path = {}", project.getBasePath());
            // 如果选中编辑器
            final DataContext dataContext = event.getDataContext();

            final Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);
            if (null != editor) {
                VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
                assert virtualFile != null;
                transform(imageMap, virtualFileMap, virtualFile, virtualFile.getName());
            } else {
                // 获取被选中的有文件和目录
                final VirtualFile[] virtualFiles = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
                if (null != virtualFiles) {
                    for (VirtualFile rootFile : virtualFiles) {
                        if (ImageContents.IMAGE_TYPE_NAME.equals(rootFile.getFileType().getName())) {
                            transform(imageMap, virtualFileMap, rootFile, rootFile.getName());

                        }
                        // 如果是目录, 则递归获取所有 image 文件
                        if (rootFile.isDirectory()) {
                            List<VirtualFile> imageFiles = ImageUtils.recursivelyImageFile(rootFile);
                            for (VirtualFile subFile : imageFiles) {
                                transform(imageMap, virtualFileMap, subFile, subFile.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 将 VirtualFile 转换为 File
     *
     * @param imageMap    the image map
     * @param virtualFile the virtual file
     * @param name        the name
     */
    private void transform(@NotNull Map<String, File> imageMap,
                           @NotNull Map<String, VirtualFile> virtualFileMap,
                           @NotNull VirtualFile virtualFile,
                           String name) {
        File out = ImageUtils.buildTempFile(virtualFile.getName());
        try {
            FileUtils.copyToFile(virtualFile.getInputStream(), out);
            imageMap.put(name, out);
            virtualFileMap.put(name, virtualFile);
        } catch (IOException e) {
            log.trace("", e);
        }
    }
}
