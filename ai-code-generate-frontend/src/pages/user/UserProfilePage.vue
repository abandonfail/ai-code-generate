<template>
  <div class="UserProfilePage">
    <a-card title="个人中心" :bordered="false">
      <a-tabs default-active-key="info">
        <!-- 基础信息 -->
        <a-tab-pane key="info" tab="基础信息">
          <a-form
            :model="loginUserStore.loginUser"
            :rules="rules"
            layout="vertical"
            ref="infoFormRef"
            @finish="onUpdateInfo"
            style="max-width: 600px"
          >
            <a-form-item label="头像">
              <a-upload
                name="userAvatar"
                list-type="picture-card"
                :show-upload-list="false"
                :before-upload="handleAvatarUpload">
                <a-avatar
                :size="100"
                :src="loginUserStore.loginUser.userAvatar || defaultAvatar"
                style="cursor: pointer"
                />
              </a-upload>
            </a-form-item>

            <a-form-item label="ID">
              <a-input v-model:value="loginUserStore.loginUser.id" disabled />
            </a-form-item>

            <a-form-item label="账号">
              <a-input v-model:value="loginUserStore.loginUser.userAccount" disabled />
            </a-form-item>

            <a-form-item label="昵称" name="userName">
              <a-input v-model:value="loginUserStore.loginUser.userName" placeholder="请输入昵称" />
            </a-form-item>

            <a-form-item label="个人简介" name="userProfile">
              <a-textarea
                v-model:value="loginUserStore.loginUser.userProfile"
                placeholder="介绍一下自己吧"
                rows="3"
              />
            </a-form-item>

            <a-form-item label="会员编号" v-if="loginUserStore.loginUser.vipNumber">
              <span>{{ loginUserStore.loginUser.vipNumber || '未开通' }}</span>
            </a-form-item>

            <a-form-item label="会员到期时间" v-if="loginUserStore.loginUser.vipExpireTime">
              <span>
                {{ loginUserStore.loginUser.vipExpireTime
                ? dayjs(loginUserStore.loginUser.vipExpireTime).format('YYYY-MM-DD HH:mm:ss')
                : '未知' }}
              </span>
            </a-form-item>

            <a-form-item>
              <a-button type="primary" html-type="submit">保存修改</a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- 修改密码 -->
        <a-tab-pane key="password" tab="修改密码">
          <a-form
            :model="passwordForm"
            :rules="passwordRules"
            layout="vertical"
            ref="pwdFormRef"
            @finish="onChangePassword"
            style="max-width: 400px"
          >
            <a-form-item label="当前密码" name="currentPassword">
              <a-input-password v-model:value="passwordForm.currentPassword" />
            </a-form-item>

            <a-form-item label="新密码" name="newPassword">
              <a-input-password v-model:value="passwordForm.newPassword" />
            </a-form-item>

            <a-form-item label="确认新密码" name="confirmPassword">
              <a-input-password v-model:value="passwordForm.confirmPassword" />
            </a-form-item>

            <a-form-item>
              <a-button type="primary" html-type="submit">确认修改</a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { updateUser, userChangePassword, userUploadAvatar } from '@/api/userController'
import router from '@/router'
import dayjs from 'dayjs';
import request from '@/request'

const loginUserStore = useLoginUserStore()

// 修改密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})


// 默认头像
const defaultAvatar = 'https://joeschmoe.io/api/v1/random'

// 表单校验规则
const rules = {
  userName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度应为 2~20 字', trigger: 'blur' },
  ],
  userProfile: [{ max: 512, message: '简介最多512个字符', trigger: 'blur' }],
}



const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_, value) => {
        if (value !== passwordForm.newPassword) {
          return Promise.reject('两次输入密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur',
    },
  ],
}

// 表单引用
const infoFormRef = ref()
const pwdFormRef = ref()

// 头像上传
const handleAvatarUpload = async (file: File) => {
  try {
    const formData = new FormData()
    formData.append('file', file) // 注意：字段名要与后端参数名一致，如 @RequestPart("file")

    const res = await request<API.BaseResponseBoolean>('/user/upload/avatar', {
      method: 'POST',
      data: formData,
      // 不设置 headers，会自动设置 multipart/form-data
    })

    if (res.data.code === 0 && res.data.data) {
      loginUserStore.loginUser.userAvatar = res.data.data
      message.success('头像上传成功')
      window.location.reload()
    } else {
      message.error('头像上传失败：' + (res?.data?.message || '未知错误'))
    }
  } catch (error) {
    console.error('头像上传异常', error)
    message.error('上传失败，请重试')
  }

  return false
}



// 修改信息提交
const onUpdateInfo = async () => {
  try {
    const res = await updateUser({
      id: loginUserStore.loginUser.id,
      userName: loginUserStore.loginUser.userName,
      userProfile: loginUserStore.loginUser.userProfile,
      userAvatar: loginUserStore.loginUser.userAvatar,
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('信息修改成功')
    } else {
      message.error('信息修改失败：' + (res?.data?.message || '未知错误'))
    }
  } catch (error) {
    console.error('信息修改异常', error)
    message.error('修改失败，请重试')
  }
}


// 修改密码提交
const onChangePassword = async () => {
  try {
    const body: API.UserChangePasswordRequest = {
      id: loginUserStore.loginUser.id,
      oldPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
      checkPassword: passwordForm.confirmPassword,
    }

    const res = await userChangePassword(body)

    if (res.data.code === 0 && res.data.data === true) {
      message.success('密码修改成功，请重新登录')
      passwordForm.currentPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      await router.push('/user/login')
    } else {
      message.error('密码修改失败：' + (res.data.message || '未知错误'))
    }
  } catch (error) {
    console.error('修改密码异常', error)
    message.error('修改失败，请重试')
  }
}

</script>

<style scoped>
.UserProfilePage {
  padding: 24px;
  background: #fff;
}
</style>
