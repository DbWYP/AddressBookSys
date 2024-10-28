<template>
	<div>
		<h1>个人通讯录系统</h1>
		<!-- 修改信息对话框 -->
		<el-dialog :visible.sync="isModalVisible" title="修改联系人" @close="closeModal">
			<el-form>
				<el-form-item label="姓名">
					<el-input v-model="modalName"></el-input>
				</el-form-item>
				<el-form-item label="电话">
					<el-input v-model="modalPhone"></el-input>
				</el-form-item>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<el-button @click="closeModal">取 消</el-button>
				<el-button type="primary" @click="confirmModification">确 定</el-button>
			</span>
		</el-dialog>

		<!-- 顶部消息提示 -->
		<el-alert v-if="alertMessage" :title="alertMessage" type="success" show-icon></el-alert>

		<el-form label-width="80px">
			<el-form-item label="姓名">
				<el-input v-model="name" placeholder="请输入姓名"></el-input>
			</el-form-item>

			<el-form-item label="电话">
				<el-input v-model="phone" placeholder="请输入电话号码"></el-input>
			</el-form-item>

			<el-form-item>
				<el-button type="primary" @click="addContact">添加联系人</el-button>
				<el-button type="danger" @click="deleteContact">删除联系人</el-button>
				<el-button type="info" @click="queryContact">查询联系人</el-button>
				<el-button type="warning" @click="modifyContact">修改联系人</el-button>
				<el-button type="success" @click = "showUseInfo">查看使用规则</el-button>
			</el-form-item>
		</el-form>

		<el-input type="textarea" v-model="displayArea" rows="10" readonly></el-input>

		<div :class="{'success': messageStatus === 'success', 'error': messageStatus === 'error'}">
			{{ message }}
		</div>
	</div>
</template>

<script>
	import axios from 'axios';

	export default {
		name: 'ContactApp',
		data() {
			return {
				name: '',
				phone: '',
				displayArea: '', // 用于展示后端返回的消息
				message: '',
				messageStatus: '',
				alertMessage: '', // 用于显示操作成功的信息
				isModalVisible: false, // 默认不显示修改对话框
				oldInfo: '',
				modalName: '', //修改后的新名字
				modalPhone: '' //修改后的新电话
			};
		},
		methods: {
			addContact() {
				this.$axios.post('/contacts/add', {
						name: this.name,
						phone: this.phone
					})
					.then(response => {
						this.handleResponse(response.data);
						this.alertMessage = response.data.msg; // 设置顶部消息
						this.displayArea = response.data.data; // 展示返回的信息
					})
					.catch(error => {
						this.handleError(error);
					});
			},
			deleteContact() {
				this.$axios.post('/contacts/delete', {
						name: this.name,
						phone: this.phone
					})
					.then(response => {
						this.handleResponse(response.data);
						this.alertMessage = response.data.msg; // 设置顶部消息
						this.displayArea = response.data.data; // 展示返回的信息
					})
					.catch(error => {
						this.handleError(error);
					});
			},
			queryContact() {
				this.$axios.post('/contacts/query', {
						name: this.name,
						phone: this.phone
					})
					.then(response => {
						this.handleResponse(response.data);
						console.log(response.data);
						this.alertMessage = response.data.msg; // 设置顶部消息
						this.displayArea = response.data.data; // 展示返回的信息
					})
					.catch(error => {
						this.handleError(error);
					});
			},
			modifyContact() {
				// 先查询联系人信息
				this.$axios.post('/contacts/query', {
						name: this.name,
						phone: this.phone
					})
					.then(response => {
						if (response.data && response.data.data) {
							// 赋值新信息
							console.log(response.data);
							const dataParts = response.data.data.split("\n")[0].split(', ');
							if (dataParts.length === 2) {
								this.name = dataParts[0].split('：')[1]; // 提取姓名
								this.phone = dataParts[1].split('：')[1]; // 提取电话
							}

							// 提示用户进行修改（可以选择显示模态框）
							this.showEditModal(); // 显示编辑模态框
							this.oldInfo = this.phone;
							// 等待用户确认修改
							// 假设有一个方法来监听用户的确认操作
							this.onConfirm = () => {
								this.$axios.post('/contacts/modify', {
										oldInfo: this.oldInfo,
										newName: this.name,
										newPhoneNum: this.phone
									})
									.then(response => {
										this.handleResponse(response.data);
										console.log(response.data);
										this.alertMessage = response.data.msg; // 设置顶部消息
										this.displayArea = response.data.data; // 展示返回的信息
									})
									.catch(error => {
										this.handleError(error);
									});
							};
						} else {
							this.alertMessage = response.data.msg; // 设置顶部消息
						}
					})
					.catch(error => {
						this.handleError(error);
					});
			},
			showEditModal() {
				// 显示模态框
				this.isModalVisible = true; // 控制模态框的显示状态

				// 如果需要，初始化模态框的数据
				this.modalName = this.name; // 预填充旧信息
				this.modalPhone = this.phone; // 预填充旧电话
			},
			closeModal() {
				this.isModalVisible = false;
				this.name = '';
				this.phone = '';
			},
			showUseInfo(){
				this.displayArea = "欢迎使用个人通讯录管理系统！\n"
                + "添加用户请输入完整正确的姓名和电话号码！\n"
                + "查询用户支持姓名模糊查询，也支持指定手机号查询！\n"
                + "修改用户信息请输入完整正确的姓名或手机号码！\n"
                + "删除用户请输入被删除的联系人完整的姓名和电话号码！\n\n";
			},
			// 确认修改的方法
			confirmModification() {
				this.name = this.modalName; // 更新联系人的新名称
				this.phone = this.modalPhone; // 更新联系人的新电话

				this.onConfirm(); // 调用确认修改的回调
				this.closeModal(); // 关闭模态框
			},
			handleResponse(response) {
				this.messageStatus = response.status;
				this.message = response.message;
			},
			handleError(error) {
				this.messageStatus = 'error';
				this.message = '操作失败：' + error.message;
			}
		}
	};
</script>

<style scoped>
	body {
		font-family: Arial, sans-serif;
		max-width: 600px;
		margin: 0 auto;
		padding: 20px;
		background-color: #f0f0f0;
	}

	h1 {
		text-align: center;
		margin-bottom: 20px;
	}

	.success {
		color: green;
	}

	.error {
		color: red;
	}
</style>