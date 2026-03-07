package com.toolkit.hub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.toolkit.hub.entity.Quote;
import org.apache.ibatis.annotations.Mapper;

/**
 * Quote Mapper
 */
@Mapper
public interface QuoteMapper extends BaseMapper<Quote> {
}
