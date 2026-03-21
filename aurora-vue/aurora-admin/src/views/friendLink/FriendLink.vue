<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="openModel(null)"> 新增 </el-button>
      <el-button
        type="danger"
        size="small"
        icon="el-icon-delete"
        :disabled="linkIdList.length == 0"
        @click="deleteFlag = true">
        批量删除
      </el-button>
      <div style="margin-left: auto">
        <el-input
          v-model="keywords"
          prefix-icon="el-icon-search"
          size="small"
          placeholder="请输入友链名"
          style="width: 200px"
          @keyup.enter.native="searchLinks" />
        <el-button type="primary" size="small" icon="el-icon-search" style="margin-left: 1rem" @click="searchLinks">
          搜索
        </el-button>
      </div>
    </div>
    <el-table border :data="linkList" @selection-change="selectionChange" v-loading="loading">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="linkAvatar" label="链接头像" align="center" width="180">
        <template slot-scope="scope">
          <img :src="scope.row.linkAvatar" width="40" height="40" />
        </template>
      </el-table-column>
      <el-table-column prop="linkName" label="链接名" align="center" />
      <el-table-column prop="linkAddress" label="链接地址" align="center" />
      <el-table-column prop="linkIntro" label="链接介绍" align="center" />
      <el-table-column prop="isReview" label="是否审核" align="center">
        <template slot-scope="scope">
          {{ scope.row.isReview === 1 ? '审核通过' : scope.row.isReview === 0 ? '未审核' : '审核未通过' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="140" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right: 5px" />
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="openModel(scope.row)"> 编辑 </el-button>
          <el-popconfirm title="确定删除吗？" style="margin-left: 1rem" @confirm="deleteLink(scope.row.id)">
            <el-button size="mini" type="danger" slot="reference"> 删除 </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pagination-container"
      background
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page="current"
      :page-size="size"
      :total="count"
      :page-sizes="[10, 20]"
      layout="total, sizes, prev, pager, next, jumper" />
    <el-dialog :visible.sync="deleteFlag" width="30%">
      <div class="dialog-title-container" slot="title"><i class="el-icon-warning" style="color: #ff9900" />提示</div>
      <div style="font-size: 1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="deleteFlag = false">取 消</el-button>
        <el-button type="primary" @click="deleteLink(null)"> 确 定 </el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="addOrEdit" width="30%">
      <div class="dialog-title-container" slot="title" ref="linkTitle" />
      <el-form label-width="80px" size="medium" :model="linkForm">
        <el-form-item label="链接名">
          <el-input style="width: 250px" v-model="linkForm.linkName" />
        </el-form-item>
        <el-form-item label="链接头像">
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-input style="width: 250px" v-model="linkForm.linkAvatar" placeholder="请输入头像链接或点击上传" />
            <el-upload
              action="/api/links/avatar"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleUploadError"
              accept="image/jpeg,image/jpg,image/png,image/gif">
              <el-button size="small" type="primary" :loading="uploading">
                {{ uploading ? '上传中' : '上传' }}
              </el-button>
            </el-upload>
          </div>
          <div v-if="linkForm.linkAvatar" style="margin-top: 10px;">
            <img :src="linkForm.linkAvatar" width="60" height="60" style="border-radius: 4px;" />
          </div>
        </el-form-item>
        <el-form-item label="链接地址">
          <el-input style="width: 250px" v-model="linkForm.linkAddress" />
        </el-form-item>
        <el-form-item label="链接介绍">
          <el-input style="width: 250px" v-model="linkForm.linkIntro" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="linkForm.isReview" placeholder="请选择审核状态" style="width: 250px">
            <el-option label="未审核" :value="0"></el-option>
            <el-option label="审核通过" :value="1"></el-option>
            <el-option label="审核未通过" :value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditCategory"> 确 定 </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.current = this.$store.state.pageState.friendLink
    this.listLinks()
  },
  data: function () {
    return {
      loading: true,
      deleteFlag: false,
      addOrEdit: false,
      uploading: false,
      linkIdList: [],
      linkList: [],
      linkForm: {
        id: null,
        linkName: '',
        linkAvatar: '',
        linkIntro: '',
        linkAddress: '',
        isReview: ''
      },
      keywords: null,
      current: 1,
      size: 10,
      count: 0
    }
  },
  methods: {
    selectionChange(linkList) {
      this.linkIdList = []
      linkList.forEach((item) => {
        this.linkIdList.push(item.id)
      })
    },
    searchLinks() {
      this.current = 1
      this.listLinks()
    },
    sizeChange(size) {
      this.size = size
      this.listLinks()
    },
    currentChange(current) {
      this.current = current
      this.$store.commit('updateFriendLinkPageState', current)
      this.listLinks()
    },
    deleteLink(id) {
      var param = {}
      if (id == null) {
        param = { data: this.linkIdList }
      } else {
        param = { data: [id] }
      }
      this.axios.delete('/api/admin/links', param).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: '成功',
            message: data.message
          })
          this.listLinks()
        } else {
          this.$notify.error({
            title: '失败',
            message: data.message
          })
        }
        this.deleteFlag = false
      })
    },
    openModel(link) {
      if (link != null) {
        this.linkForm = JSON.parse(JSON.stringify(link))
        this.$refs.linkTitle.innerHTML = '修改友链'
      } else {
        this.linkForm.id = null
        this.linkForm.linkName = ''
        this.linkForm.linkAvatar = ''
        this.linkForm.linkIntro = ''
        this.linkForm.linkAddress = ''
        this.linkForm.isReview = null
        this.$refs.linkTitle.innerHTML = '添加友链'
      }
      this.addOrEdit = true
    },
    addOrEditCategory() {
      console.log("提交的数据：", this.linkForm); // 打印提交的数据
      if (this.linkForm.linkName.trim() == '') {
        this.$message.error('友链名不能为空')
        return false
      }
      if (this.linkForm.linkAvatar.trim() == '') {
        this.$message.error('友链头像不能为空')
        return false
      }
      if (this.linkForm.linkIntro.trim() == '') {
        this.$message.error('友链介绍不能为空')
        return false
      }
      if (this.linkForm.linkAddress.trim() == '') {
        this.$message.error('友链地址不能为空')
        return false
      }
      if (this.linkForm.isReview === '') {
        this.$message.error('审核状态不能为空')
        return false
      }
      this.axios.post('/api/admin/links', this.linkForm).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: '成功',
            message: data.message
          })
          this.listLinks()
        } else {
          this.$notify.error({
            title: '失败',
            message: data.message
          })
        }
        this.addOrEdit = false
      })
    },
    listLinks() {
      this.axios
        .get('/api/admin/links', {
          params: {
            current: this.current,
            size: this.size,
            keywords: this.keywords
          }
        })
        .then(({ data }) => {
          this.linkList = data.data.records
          this.count = data.data.count
          this.loading = false
        })
    },
    beforeAvatarUpload(file) {
      const isImage = /^image\/(jpeg|jpg|png|gif)$/.test(file.type)
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.warning('只能上传 JPG/PNG/GIF 格式的图片')
        return false
      }
      if (!isLt2M) {
        this.$message.warning('图片大小不能超过 2MB')
        return false
      }
      this.uploading = true
      return true
    },
    handleAvatarSuccess(response) {
      this.uploading = false
      if (response.flag) {
        this.linkForm.linkAvatar = response.data
        this.$message.success('图片上传成功')
      } else {
        this.$message.error(response.message || '图片上传失败')
      }
    },
    handleUploadError() {
      this.uploading = false
      this.$message.error('图片上传失败，请重试')
    }
  }
}
</script>
