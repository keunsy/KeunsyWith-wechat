CREATE TABLE `function` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(50) NOT NULL COMMENT '关键词',
  `title` varchar(200) DEFAULT NULL COMMENT '回复',
  `desc` varchar(200) DEFAULT NULL COMMENT '描述',
  `url` varchar(200) DEFAULT NULL COMMENT '链接地址',
  `pic_url` varchar(200) DEFAULT NULL COMMENT '图片地址',
  `allow_user` varchar(50) DEFAULT NULL COMMENT '允许操作的用户，默认所有人都可以操作',
  `class_name` varchar(50) DEFAULT 'AskReplyProcess' COMMENT '反射的类名',
  `type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '类型 1：显示 2：隐藏',
  `match_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '关键词匹配类型 1：完全匹配 2：前缀匹配',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;