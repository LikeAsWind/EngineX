package org.nstep.engine.module.system.convert.social;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.nstep.engine.module.system.api.social.dto.SocialUserBindReqDTO;
import org.nstep.engine.module.system.controller.admin.socail.vo.user.SocialUserBindReqVO;

@Mapper
public interface SocialUserConvert {

    SocialUserConvert INSTANCE = Mappers.getMapper(SocialUserConvert.class);

    @Mapping(source = "reqVO.type", target = "socialType")
    SocialUserBindReqDTO convert(Long userId, Integer userType, SocialUserBindReqVO reqVO);

}
