package com.bwc.message.v1.sender.dao.lgu.v1;

import org.apache.ibatis.annotations.Mapper;

import com.bwc.message.v1.sender.dao.SenderMessageDAO;

@Mapper
public interface LguV1MessageDAO extends SenderMessageDAO {
	// method는 상속, Database connection 분할을 위해 DAO 분리
}
