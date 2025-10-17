package top.duhongming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 卡片
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardDTO {
    private String template_id;
    private Map<String, Object> template_variable;
}
