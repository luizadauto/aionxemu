-- ----------------------------
-- Legion_History-History_Type-Enum Fix 
-- Desc: Run if Server between revisions (r1 to r17)
-- ----------------------------
ALTER TABLE `legion_history` CHANGE COLUMN `history_type` `history_type` enum('CREATE','JOIN','KICK','LEVEL_UP','APPOINTED','EMBLEM_REGISTER','EMBLEM_MODIFIED') NOT NULL;
