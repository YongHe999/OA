package com.seu.chat.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seu.chat.mapper.ChatMsgMapper;
import com.seu.chat.service.ChatMsgService;
import com.seu.util.entity.ChatMsg;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-23
 */
@Service
public class ChatMsgServiceImpl extends ServiceImpl<ChatMsgMapper, ChatMsg> implements ChatMsgService {

}
