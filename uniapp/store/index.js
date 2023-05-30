import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex)
//需要在main.js调用该文件
const store = new Vuex.Store({
    //data
    state: {
        userInfo: {},
        needAuth: true,
        isLogin: false,
    },
    //computed
    getters: {
        //获取用户信息
        getUserInfo(state) {
            return state.userInfo;
        },
        //标记是否需要授权
        getNeedAuth(state) {
            return state.needAuth;
        },
        //标记是否登录
        getIsLogin(state) {
            return state.isLogin;
        },
    },
    //methods 同步的方法
    mutations: {
        //存储用户信息
        setUserInfo(state, userInfo) {
            state.userInfo = userInfo;
        },
        //更改授权状态
        setNeedAuth(state, needAuth) {
            state.needAuth = needAuth;
        },
        //更改登录状态
        setIsLogin(state, isLogin) {
            state.isLogin = isLogin;
        },
    },
    //异步方法
    actions: {
        //微信登录
        login(content) {
            return new Promise((resolve, reject) => {
                uni.login({
                    provider: 'weixin', //使用微信登录
                    success: res => {
                        console.log(res.code);
                        Vue.prototype.$u.api.login({
                            appid: Vue.prototype.appid,
                            code: res.code,
                            token: uni.getStorageSync('token')
                        }).then(res => {
                            console.log(res);
                            uni.setStorageSync('token', res.data.token);
                            if (res.msg === '登录成功') {
                                content.commit('setUserInfo', res.data.userInfo);
                                content.commit('setNeedAuth', false);
                                content.commit('setIsLogin', true);
                            }

                        })
                    },
                    fail: res => {
                        Vue.prototype.$u.toast('获取code失败');
                        reject('获取code失败');
                    }
                });
            })
        },
        //微信登录授权
        authUserInfo(content) {
            return new Promise((resolve, reject) => {
                uni.getUserInfo({
                    provider: 'weixin',
                    lang: 'zh_CN',
                    success: res => {
                        console.log("用户", JSON.stringify(res))
                        content.commit('setUserInfo', {
                            nickname: res.userInfo.nickName,
                            gender: res.userInfo.gender,
                            avatar: res.userInfo.avatarUrl,
                            city: res.userInfo.city,
                        })
                        Vue.prototype.$u.api.auth(content.state.userInfo).then(res => {
                            console.log(res)
                            if (res.msg === '授权成功') {
                                content.commit("setNeedAuth", false);
                                content.commit("setIsLogin", true);
                            } else {
                                Vue.prototype.$u.toast('授权失败');
                            }
                            resolve('授权完成')
                        })
                    },
                    fail: err => {
                        reject('获取信息报错');
                    }
                })
            });
        }
    },
})

export default store
