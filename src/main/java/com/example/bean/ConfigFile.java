/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: config
 * Author:   cj
 * Date:     2019/4/30 15:21
 * Description: 配置文件类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.bean;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 〈一句话功能简述〉<br>
 * 〈配置文件类〉
 *
 * @author cj
 * @create 2019/4/30
 * @since 1.0.0
 */
@Component
@Data
@ToString
@Validated
@ConfigurationProperties(prefix = "filepath")
public class ConfigFile {

    private  String qrFile;

}
