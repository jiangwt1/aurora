package com.aurora.strategy.context;

import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.service.SystemConfigProviderService;
import com.aurora.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.aurora.enums.SearchModeEnum.getStrategy;

@Service
public class SearchStrategyContext {

    @Autowired
    private SystemConfigProviderService configProvider;

    @Autowired
    private Map<String, SearchStrategy> searchStrategyMap;
    
    //根据提供的mode去获取对应的SearchStrategy，也就是去SearchModeEnum找对应的模型执行查询
    public List<ArticleSearchDTO> executeSearchStrategy(String keywords) {
        String searchMode = configProvider.getConfig("search.mode", "mysql");
        return searchStrategyMap.get(getStrategy(searchMode)).searchArticle(keywords);
    }

}
