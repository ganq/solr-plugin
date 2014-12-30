/**
 * Copyright mysoft Limited (c) 2014. All rights reserved.
 * This software is proprietary to and embodies the confidential
 * technology of mysoft Limited. Possession, use, or copying
 * of this software and media is authorized only pursuant to a
 * valid written license from mysoft or an authorized sublicensor.
 */
package com.mysoft.b2b.solr.db;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


/**
 * ganq: Change to the actual description of this class
 * 
 * @version Revision History
 * 
 *          <pre>
 * Author     Version       Date        Changes
 * ganq    1.0           2014年8月27日     Created
 * 
 * </pre>
 * @since b2b 2.0.0
 */

public class ScoreReader extends TimerTask {
	private static final String SPRINT_FILEPATH_CONTEXT = "META-INF/spring/spring-jdbc.xml";
    private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(SPRINT_FILEPATH_CONTEXT);
    private static final JdbcTemplate jdbcTemplate = (JdbcTemplate)appContext.getBean("jdbcSearch");


    private static Map<String,Object> scoreList = new HashMap<String, Object>();

    @Override
    public void run() {
        String sql = "select * from supplier_intervent_score ";
        try {
            scoreList = jdbcTemplate.query(sql, new ResultSetExtractor<Map<String,Object>>() {
                @Override
                public Map<String, Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
                    while (rs.next()) {
                        scoreList.put(rs.getString("supplierId"), rs.getInt("score"));
                    }
                    return scoreList;
                }
            });
        } catch (Exception e) {
            System.out.println("supplier_intervent_score表取数异常.：");
        }
    }

    /**
     * 根据供应商id查询人工干预分
     */
    public static Integer getSupplierScoreById(String supplierId) {
        if (scoreList == null){
            return 0;
        }
        return NumberUtils.toInt(ObjectUtils.toString(scoreList.get(supplierId)));

    }


	
}


