DELIMITER //

CREATE PROCEDURE generate_environment_data()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE tunnel INT;
    DECLARE loc VARCHAR(20);
    DECLARE curr_time DATETIME;
    
    -- 设置初始时间为7天前
    SET curr_time = DATE_SUB(NOW(), INTERVAL 7 DAY);
    
    -- 循环生成7天的数据
    WHILE i < (24 * 7) DO
        -- 为每个隧道生成数据
        SET tunnel = 1;
        WHILE tunnel <= 5 DO
            -- 入口数据
            INSERT INTO environment (tunnel_id, location, temperature, humidity, co2, pm25, created_at, updated_at)
            VALUES (tunnel, '隧道入口', 
                   ROUND(24.858 + (RAND() * 0.2 - 0.1), 3),
                   ROUND(67.221 + (RAND() * 0.4 - 0.2), 3),
                   463 + (RAND() * 2 - 1),
                   ROUND(45.974 + (RAND() * 0.4 - 0.2), 3),
                   curr_time, curr_time);
            
            -- 中段数据
            INSERT INTO environment (tunnel_id, location, temperature, humidity, co2, pm25, created_at, updated_at)
            VALUES (tunnel, '隧道中段', 
                   ROUND(26.458 + (RAND() * 0.2 - 0.1), 3),
                   ROUND(72.266 + (RAND() * 0.4 - 0.2), 3),
                   476 + (RAND() * 2 - 1),
                   ROUND(47.463 + (RAND() * 0.4 - 0.2), 3),
                   curr_time, curr_time);
            
            -- 出口数据
            INSERT INTO environment (tunnel_id, location, temperature, humidity, co2, pm25, created_at, updated_at)
            VALUES (tunnel, '隧道出口', 
                   ROUND(27.568 + (RAND() * 0.2 - 0.1), 3),
                   ROUND(72.170 + (RAND() * 0.4 - 0.2), 3),
                   481 + (RAND() * 2 - 1),
                   ROUND(49.214 + (RAND() * 0.4 - 0.2), 3),
                   curr_time, curr_time);
            
            SET tunnel = tunnel + 1;
        END WHILE;
        
        -- 增加1小时
        SET curr_time = DATE_ADD(curr_time, INTERVAL 1 HOUR);
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 先清空现有数据
DELETE FROM environment;

-- 执行存储过程
CALL generate_environment_data(); 