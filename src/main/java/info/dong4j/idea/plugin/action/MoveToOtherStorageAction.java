package info.dong4j.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;

import org.jetbrains.annotations.NotNull;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Company: 科大讯飞股份有限公司-四川分公司</p>
 * <p>Description: 图床迁移计划 </p>
 *
 * @author dong4j
 * @email sjdong3@iflytek.com
 * @since 2019-03-15 20:41
 */
@Slf4j
public final class MoveToOtherStorageAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        final Presentation presentation = event.getPresentation();
        presentation.setEnabled(false);
        presentation.setVisible(true);
        presentation.setIcon(AllIcons.Actions.Lightning);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
