package com.aicodegenerate.core.saver;

import cn.hutool.core.util.StrUtil;
import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;

/**
 * HTML代码文件保存器
 *
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }
}
