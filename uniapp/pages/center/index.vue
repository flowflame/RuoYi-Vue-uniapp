<template>
  <view class="content">
    <u-navbar :is-back="false" :title="title" title-color="white" :background="background"></u-navbar>
    <u-avatar :src="getUserInfo.avatar"></u-avatar>
    <text align="center">{{getUserInfo.nicknamegetUserInfo.nickname}}</text>
    <button @getuserinfo="authUserInfo" open-type="getUserInfo">授权登录</button>
  </view>
</template>

<script>
import {mapGetters, mapActions} from 'vuex'

export default {
  data() {
    return {
      title: '个人中心',
      background: {
        backgroundImage: 'linear-gradient(45deg, rgb(28, 187, 180), rgb(141, 198, 63))'
      },
      userInfo: {},
    }
  },
  onLoad() {
    if (!this.getIsLogin) {
      this.init();
    }
  },
  computed: {
    ...mapGetters(['getUserInfo', 'getNeedAuth', 'getIsLogin'])
  },
  methods: {
    ...mapActions(['login', 'authUserInfo']),
    init() {
      this.login();
    }
  },

}
</script>

<style>
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.logo {
  height: 200 rpx;
  width: 200 rpx;
  margin-top: 200 rpx;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 50 rpx;
}

.text-area {
  display: flex;
  justify-content: center;
}

.title {
  font-size: 36 rpx;
  color: #8f8f94;
}
</style>
