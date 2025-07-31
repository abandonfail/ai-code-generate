<template>
  <a-modal
    :visible="localVisible"
    title="编辑用户信息"
    @ok="onSubmit"
    :confirm-loading="loading"
    @cancel="onCancel"
    destroy-on-close
    >
    <a-form :model="form" layout="vertical">
      <a-form-item label="用户名">
        <a-input v-model:value="form.userName" />
      </a-form-item>
      <a-form-item label="简介">
        <a-input v-model:value="form.userProfile" />
      </a-form-item>
      <a-form-item label="用户角色">
        <a-select v-model:value="form.userRole">
          <a-select-option value="user">普通用户</a-select-option>
          <a-select-option value="admin">管理员</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { updateUser } from '@/api/userController'

const props = defineProps<{
  visible: boolean
  user: Partial<API.UserVO> | null
}>()

const emits = defineEmits(['update:visible', 'success'])

const localVisible = ref(false)
const form = ref<Partial<API.UserVO>>({})
const loading = ref(false)

// 同步 props.visible 到本地变量
watch(
  () => props.visible,
  (val) => {
    localVisible.value = val
  },
  { immediate: true }
)

// 同步传入的用户数据
watch(
  () => props.user,
  (val) => {
    if (val) {
      form.value = {
        ...val,
        vipExpireTime: val.vipExpireTime ? dayjs(val.vipExpireTime) : undefined,
      }
    }
  },
  { immediate: true }
)

// 提交
const onSubmit = async () => {
  loading.value = true
  const formData = {
    ...form.value,
  }

  const res = await updateUser(formData)
  loading.value = false
  if (res.data.code === 0) {
    message.success('更新成功')
    emits('update:visible', false)
    emits('success')
  } else {
    message.error('更新失败：' + res.data.message)
  }
}

const onCancel = () => {
  emits('update:visible', false)
}
</script>
