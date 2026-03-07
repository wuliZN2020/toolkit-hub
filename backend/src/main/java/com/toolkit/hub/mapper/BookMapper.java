package com.toolkit.hub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.toolkit.hub.entity.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * Book Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
