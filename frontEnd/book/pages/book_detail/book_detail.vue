<template>
	<view class="book-detail-container">
		<!-- 头部背景 -->
		<view class="headBackgroundContainer">
			<view class="headBackground" :style="backgroundStyle"></view>
		</view>

		<view class="head">
			<!-- 书籍基本信息 -->
			<view class="bookInfo">
				<image class="headImg" :src="bookDetail.headerImage" mode="aspectFill"></image>
				<view class="left-text">
					<view class="bookName">{{ bookDetail.name }}</view>
					<view class="author">作者：{{ bookDetail.author }}</view>
					<view class="wordsNum">字数：348.33万</view>
					<view class="source">最新章节：{{ bookDetail.newest }}</view>
					<view class="updatetime">更新时间：{{ bookDetail.updateTime }}</view>
				</view>
			</view>
			<view class="clearfix"></view>

			<!-- 操作按钮组 -->
			<view class="btn-group">
				<view class="addToList" @click="addOrRemove">
					<!-- v-if/v-else 更清晰地表达互斥状态 -->
					<view v-if="!isInList">
						<icon class="addList-icon" />加入书架
					</view>
					<view v-else>
						<icon type="success_no_circle" color="#fd9941" size="20" />从书架中移除
					</view>
				</view>
				<view class="read" @click="goToReader">
					<image src="../../static/read1.png"></image>立即阅读
				</view>
			</view>
		</view>

		<!-- 书籍简介 -->
		<view class="book-des">
			<view class="des-content" @click="toggleShowAllDes">
				<!-- 使用 computed 属性简化模板逻辑 -->
				{{ displayDescription }}
				<text class="read-more" v-if="bookDetail.shortDes && !showAllDes">阅读全部</text>
			</view>
		</view>

		<view class="splitor"></view>

		<!-- 精彩评论 -->
		<view class="comments">
			<view class="title">
				精彩评论
				<text class="add-comment" @click="toWriteComment">写评论</text>
			</view>
			<view class="comment-list">
				<!-- 使用 v-if/v-else 处理列表为空的情况 -->
				<template v-if="comments.length > 0">
					<!-- 找到 .comment-item 循环 -->
					<view class="comment-item" v-for="item in comments" :key="item.rootComment.commentid">
					    <image class="headImg" :src="item.rootComment.avatar" mode="scaleToFill"></image>
					    <view class="comment-right">
					        <!-- [新增] 包裹头部信息的容器 -->
					        <view class="comment-header">
					            <!-- [新增] 包裹用户名和时间的容器 -->
					            <view class="reader-meta">
					                <view class="reader-name">{{ item.rootComment.nickname }}</view>
					                <view class="create-time">{{ item.rootComment.time }}</view>
					            </view>
					            <!-- 工具栏：点赞、回复 (位置移动到这里) -->
					            <view class="toolbar">
					                <icon 
					                    :class="['iconfont', item.rootComment.isLike ? 'icon-selectetd' : 'icon-like']" 
					                    @click="toggleLike(item.rootComment)" 
					                />
					                <text class="likeNum">{{ item.rootComment.likenum || 0 }}</text>
					                <icon 
					                    class="iconfont reply" 
					                    @click="toWriteComment($event, item.rootComment)" 
					                />
					            </view>
					        </view>
					        
					        <!-- 评论内容 (位置不变) -->
					        <view class="comment">{{ item.rootComment.content }}</view>
					        
					        <!-- 查看更多回复 (位置不变) -->
					        <view class="comment-more" v-if="item.child.length > 0" @click="toggleMoreComments(item.rootComment)">
					            {{ '查看' + item.child.length + '条回复 >' }}
					        </view>
					        
					        <!-- 子评论列表 (位置不变) -->
					        <view class="childComments" v-if="item.rootComment.isOpenMoreComment && item.child.length > 0">
					            <view class="child-item" v-for="childItem in item.child" :key="childItem.commentid" @click="toWriteComment($event, childItem)">
					                {{ childItem.nickname }} 回复 {{ childItem.reply }} ： {{ childItem.content }}
					                <text class="childItem-time">{{ childItem.time }}</text>
					            </view>
					        </view>
					
					    </view>
					</view>
				</template>
				<!-- 评论为空时的占位 -->
				<view class="no-comments" v-else>
					<image src="https://wk-gulimall.oss-cn-beijing.aliyuncs.com/shafa.jfif"></image>
					暂无评论，快去抢沙发...
				</view>
			</view>
		</view>

		<!-- 评论输入框，使用 v-show 性能更好，因为它只是切换 display -->
		<view class="commnet-input-bar" v-show="!commentInputHide">
			<input 
				class="comment-input" 
				v-model="currentCommentValue"
				:placeholder="commentPlaceholder" 
				placeholder-class="commentInputP" 
				:focus="!commentInputHide" 
				maxlength="100" 
				@blur="hideCommentBar" 
				@confirm="sendComment" 
				confirm-type="send"
			/>
			<view class="cancle" @click="hideCommentBar">取消</view>
		</view>

		<!-- 以下 WXML 引入保持不变 -->
		<import src="../component/err_tips/err_tips.wxml" />
		<template is="err_tips" :data="err_tips_data" />
		<import src="../component/toast/toast.wxml" />
		<template is="toast" :data="toast_data" />
		<import src="../component/err_page/err_page.wxml" />
		<template is="err_page" :data="err_page_data" />
	</view>
</template>

<script>
	import Api from '../../utils/api/api.js';
	import Util from '../../utils/util.js';

	export default {
		data() {
			return {
				bookid: null,
				bookDetail: {}, // 初始化为空对象，避免模板渲染时出错
				isInList: 0, // 0-不在书架, 1-在书架
				showAllDes: false,
				comments: [],

				// 评论相关状态
				commentInputHide: true,
				currentCommentValue: '',
				commentTarget: null, // 用于存储回复的目标信息 { id, nickname, userid }

				// 页面状态
				err_page_data: null,
				toast_data: null, // 确保 toast_data 在 data 中定义
				err_tips_data: null, // 确保 err_tips_data 在 data 中定义
				
				// 用户信息
				token: "",
				userid: ""
			};
		},
		computed: {
			/**
			 * 动态计算头部背景样式
			 */
			backgroundStyle() {
				if (this.bookDetail && this.bookDetail.headerImage) {
					return `background: url(${this.bookDetail.headerImage}) no-repeat center; background-size: 200%;`;
				}
				return '';
			},
			/**
			 * 计算需要显示的书籍描述
			 */
			displayDescription() {
				if (!this.bookDetail || !this.bookDetail.des) {
					return '本书是一种介绍编程语言、技术或概念的书籍，通常包含代码示例、练习和项目，帮助读者学习和实践编程。编程书有不同的难度级别，适合不同的读者群体，从初学者到专家。';
				}
				if (this.showAllDes || !this.bookDetail.shortDes) {
					return this.bookDetail.des;
				}
				return this.bookDetail.shortDes;
			},
			/**
			 * 评论输入框的占位符
			 */
			commentPlaceholder(){
				if(this.commentTarget && this.commentTarget.id !== 'root'){
					return `回复 ${this.commentTarget.nickname}：`;
				}
				return '发表评论';
			}
		},
		onLoad(options) {
			this.bookid = options.bookid;
			this.token = uni.getStorageSync('token');
			
			uni.showLoading({ title: '加载中...' });

			// 并行获取用户ID和书籍详情
			this.fetchUserId();
			this.fetchBookDetail();
			this.fetchComments();
		},
		methods: {
			// --- 数据获取方法 ---
			fetchUserId() {
				if (!this.token) return;
				uni.request({
					url: Api.GetUserId(this.token),
					success: (res) => {
						if (res.data && res.data.code === 200) {
							this.userid = res.data.data;
						}
					}
				});
			},
			fetchBookDetail() {
				uni.request({
					url: Api.getBookDetail(this.bookid),
					success: (res) => {
						const { data: tmpData } = res;
						if (tmpData && tmpData.code === 200) {
							let detail = tmpData.data;
							// 格式化数据
							detail.updateTime = Util.formatDate3(new Date(detail.updateTime));
							detail.des = detail.des.replace(/( ){2,}/, ' ');
							detail.shortDes = detail.des.length > 95 ? detail.des.substring(0, 76) : '';
							detail.name = detail.factionName;

							this.bookDetail = detail;
							uni.setNavigationBarTitle({ title: detail.name });
							this.checkIfInBookshelf(); // 获取书籍详情后检查是否在书架
							this.err_page_data = null; // 清除错误页面
						} else {
							this.showErrorPage('请求书籍信息失败');
						}
					},
					fail: (err) => {
						console.error('fetchBookDetail fail:', err);
						this.showErrorPage('努力找不到网络>_<请检查后重试');
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},
			fetchComments() {
				uni.request({
					url: `${Api.listComments(this.bookid)}&token=${this.token}`,
					success: (res) => {
						const { data: tmpData } = res;
						if (tmpData && tmpData.code === 200) {
							this.comments = tmpData.data.map(item => {
								// 格式化根评论
								item.rootComment.time = Util.formatDate3(new Date(item.rootComment.time));
								if (item.rootComment.userid == this.userid) {
									item.rootComment.nickname = "我";
								}
								// 默认不展开子评论
								item.rootComment.isOpenMoreComment = false;
								
								// 格式化子评论
								item.child.forEach(childItem => {
									childItem.time = Util.formatDate3(new Date(childItem.time));
									if (childItem.userid == this.userid) {
										childItem.nickname = "我";
									}
								});
								return item;
							});
						} else {
							Util.showErrMsg(this, '请求评论失败!', 1000);
						}
					},
					fail: (err) => {
						console.error('fetchComments fail:', err);
						Util.showErrMsg(this, '请求评论失败!', 1000);
					}
				});
			},
			checkIfInBookshelf() {
				if (!this.token) return;
				uni.request({
					url: Api.GetJudgeIsInList(this.token, this.bookid),
					success: (res) => {
						if (res.data && res.data.code === 200) {
							this.isInList = res.data.data ? 1 : 0;
						}
					}
				});
			},

			// --- 页面交互方法 ---
			toggleShowAllDes() {
				if (this.bookDetail.shortDes) {
					this.showAllDes = !this.showAllDes;
				}
			},
			goToReader() {
				console.log('goToReader 被点了', this.bookid)
				const { bookid, bookDetail } = this
				  const name  =bookDetail.name   // 中文参数最好编码
				  console.log(bookid,name)
				  uni.navigateTo({
				    url: `/pages/book_detail/reader/reader?bookid=${bookid}&name=${name}`
				  })
			},
			addOrRemove() {
				const inList = this.isInList;
				const url = inList ? Api.deleteMyBooks() : Api.addMyBooks();
				const data = { userid: this.userid, bookid: this.bookid };
				
				uni.request({
					method: 'POST',
					url,
					data,
					success: (res) => {
						const { data: tmpData } = res;
						if (tmpData && tmpData.code === 200) {
							this.isInList = inList ? 0 : 1;
							uni.showToast({
								title: inList ? '已从书架移除' : '已添加至书架',
								icon: 'success'
							});
						} else {
							const errorMsg = inList ? '移除失败' : (tmpData.code === 400 ? '本书已在书架' : '添加失败');
							if(errorMsg === '本书已在书架') this.isInList = 1; // 修正状态
							Util.showErrMsg(this, errorMsg, 1500);
						}
					},
					fail: () => Util.showErrMsg(this, inList ? '移除失败' : '添加失败', 1000)
				});
			},

			// --- 评论相关方法 ---
			/**
			 * 切换评论的点赞状态
			 * @param {object} comment - 被操作的评论对象 (rootComment)
			 */
			toggleLike(comment) {
				const isLiked = comment.isLike;
				const url = isLiked ? Api.RemoveLikeNum(this.token) : Api.addLikeNum();
				const data = { bookid: this.bookid, commentid: comment.commentid, userid: this.userid };
				
				uni.request({
					method: 'POST',
					url,
					data,
					success: (res) => {
						if (res.data && res.data.code === 200) {
							if(!isLiked && res.data.data === 0){
								uni.showToast({ title: '已经点赞', icon: 'none' });
								comment.isLike = 1; // 修正状态
								return;
							}
							comment.isLike = isLiked ? 0 : 1;
							comment.likenum += isLiked ? -1 : 1;
						} else {
							Util.showErrMsg(this, '操作失败!', 1000);
						}
					},
					fail: () => Util.showErrMsg(this, '操作失败!', 1000)
				});
			},

			toggleMoreComments(comment) {
				comment.isOpenMoreComment = !comment.isOpenMoreComment;
			},
			
			/**
			 * 准备写评论
			 * @param {Event} e - 事件对象
			 * @param {object} target - 回复的目标评论对象，不传则为根评论
			 */
			toWriteComment(e, target) {
				if (target) {
					// 回复特定评论
					this.commentTarget = { id: target.commentid, nickname: target.nickname, userid: target.userid };
				} else {
					// 发表新评论
					this.commentTarget = { id: 'root', nickname: '', userid: '' };
				}
				this.commentInputHide = false;
			},
			hideCommentBar() {
				this.commentInputHide = true;
				this.currentCommentValue = ''; // 清空输入
				this.commentTarget = null;     // 重置目标
			},
			
			sendComment() {
				const content = this.currentCommentValue.trim();
				if (!content) {
					Util.showErrMsg(this, '评论内容不能为空', 1000);
					return;
				}
				
				if (this.userid === this.commentTarget?.userid) {
					Util.showErrMsg(this, '请勿给自己回复!', 1000);
					return;
				}

				const addCommentData = {
					userid: this.userid,
					bookid: this.bookid,
					father: this.commentTarget.id,
					content: content
				};
				
				uni.request({
					url: Api.addComment(),
					method: 'POST',
					data: JSON.stringify(addCommentData),
					success: (res) => {
						if (res.data && res.data.code === 200) {
							uni.showToast({ title: '发布成功', icon: 'success' });
							this.hideCommentBar();
							// 刷新评论列表以获取最新数据
							this.fetchComments();
						} else {
							Util.showErrMsg(this, '发布评论失败', 1000);
						}
					},
					fail: () => Util.showErrMsg(this, '发布评论失败', 1000)
				});
			},

			// --- 辅助方法 ---
			showErrorPage(text, buttonText = '重试', clickMethod = 'fetchBookDetail') {
				this.err_page_data = {
					show: true,
					image_url: 'https://file.lantingshucheng.com/myapp/err_tips/network_err.png',
					text: text,
					buttonText: buttonText,
					click: clickMethod
				};
			}
		}
	}
</script>
<style lang="stylus">
/* --- 变量定义 --- */
$primary-color = #fd9941
$primary-color-light = #fff4ea
$text-color-base = #333
$text-color-secondary = #666
$text-color-tertiary = #999
$bg-color = #ffffff
$bg-color-alt = #f5f5f7
$border-color = #f0f0f0 // 使用更浅的边框色
$border-radius-sm = 8rpx
$border-radius-md = 12rpx
$shadow-light = 0 4rpx 12rpx rgba(0, 0, 0, 0.08)

/* --- 图标字体 --- */
.reply::before
	content: "\e644"
	font-size: 44rpx
	color: $text-color-secondary

.icon-selectetd::before
	content: '\e8c3'
	color: $primary-color
	font-size: 40rpx

.icon-like::before
	content: '\e8ad'
	color: $text-color-secondary
	font-size: 40rpx

/* --- 全局与错误页 --- */
.err_page
	position: absolute
	z-index: 10000
	background: $bg-color
	height: 100%
	width: 100%
	top: 0
	left: 0
	display: flex
	flex-direction: column
	justify-content: center
	align-items: center
	color: $text-color-tertiary
	font-size: 30rpx
	
	image
		width: 330rpx
		height: 330rpx
		margin-bottom: 30rpx
	
	button
		margin-top: 40rpx
		width: 200rpx
		line-height: 64rpx
		height: 64rpx
		font-size: 32rpx
		color: $primary-color
		border: 2rpx solid $primary-color
		border-radius: $border-radius-sm
		background-color: transparent

/* --- 主容器 --- */
.book-detail-container
	background-color: $bg-color-alt
	font-size: 28rpx
	line-height: 1.7
	color: $text-color-base

/* --- 头部区域 --- */
.headBackgroundContainer
	position: absolute
	top: 0
	left: 0
	width: 100%
	height: 484rpx
	overflow: hidden
	z-index: 0

	.headBackground
		width: 110%
		height: 600rpx
		filter: blur(30rpx) brightness(0.7)
		transform: translate(-28rpx, -28rpx)

.head
	position: relative
	z-index: 1
	padding: 40rpx 30rpx
	color: #fff
	display: flex
	flex-direction: column
	gap: 40rpx

	.bookInfo
		display: flex
		gap: 30rpx

		.headImg
			width: 210rpx
			height: 290rpx
			border-radius: $border-radius-md
			box-shadow: $shadow-light
			flex-shrink: 0

		.left-text
			display: flex
			flex-direction: column
			justify-content: space-around
			color: rgba(255, 255, 255, 0.9)
			font-size: 24rpx
			width: 400rpx

			.bookName
				font-size: 40rpx
				font-weight: bold
				color: #fff
				display: -webkit-box
				-webkit-line-clamp: 2
				-webkit-box-orient: vertical  
				overflow: hidden
				text-overflow: ellipsis
				line-height: 1.3
			
			.author, .wordsNum, .source, .updatetime
				white-space: nowrap
				overflow: hidden
				text-overflow: ellipsis

	.btn-group
		display: flex
		gap: 30rpx
		
		> view
			flex: 1
			display: flex
			justify-content: center
			align-items: center
			height: 80rpx
			border-radius: $border-radius-md
			font-size: 28rpx
			font-weight: 500
			transition: all 0.2s ease
		
		/* --- [额外优化] “加入书架”按钮样式 --- */
		.addToList
			background-color: rgba(255, 255, 255, 0.2) // 改为半透明背景
			color: #fff // 文字用白色
			border: 1rpx solid rgba(255, 255, 255, 0.7) // 增加一个细边框
			
			icon
				margin-right: 12rpx
		
		.read
			background-color: $primary-color
			color: #fff
			box-shadow: 0 4rpx 10rpx alpha($primary-color, 0.4)
			border: none
			
			image
				width: 38rpx
				height: 38rpx
				margin-right: 12rpx

/* --- 通用分隔符 --- */
.splitor
	height: 20rpx
	background-color: $bg-color-alt

/* --- 书籍简介 --- */
.book-des
	background-color: $bg-color
	padding: 30rpx
	
	.des-content
		color: $text-color-secondary
		font-size: 28rpx
		text-align: justify
		
		.read-more
			color: $primary-color
			font-weight: bold
			margin-left: 10rpx


/* ======================================================= */
/* --- 评论区美化 --- */
/* ======================================================= */
.comments
	background-color: $bg-color
	padding-bottom: 20rpx
	
	// 评论区标题
	.title
		display: flex
		justify-content: space-between
		align-items: center
		padding: 30rpx
		height: auto
		line-height: 1
		font-size: 34rpx
		font-weight: bold
		border-left: 8rpx solid $primary-color // 增加主题色块，提升设计感
		border-bottom: 1rpx solid $border-color

		.add-comment
			font-size: 28rpx
			color: $primary-color
			font-weight: normal

	// 评论列表
	.comment-list
		padding-bottom: 120rpx

	// 暂无评论
	.no-comments
		display: flex
		flex-direction: column
		align-items: center
		justify-content: center
		padding: 100rpx 0
		color: $text-color-tertiary
		font-size: 28rpx
		
		image
			width: 120rpx
			height: 120rpx
			margin-bottom: 20rpx
			opacity: 0.7

	// 单条评论项
	.comment-item
		display: flex
		gap: 20rpx
		padding: 40rpx 30rpx // 增加垂直间距
		border-bottom: 1rpx solid $border-color
		
		&:last-child
			border-bottom: none

		.headImg
			width: 70rpx
			height: 70rpx
			border-radius: 50%
			flex-shrink: 0
		
		.comment-right
			flex: 1
			display: flex
			flex-direction: column
			gap: 16rpx // 调整元素间距
			
			// 用户名、时间、操作按钮的容器
			.comment-header
				display: flex
				justify-content: space-between
				align-items: center
				
				.reader-meta // 组合用户名和时间
					display: flex
					flex-direction: column
					gap: 6rpx
					
					.reader-name
						color: $text-color-secondary
						font-size: 28rpx
						font-weight: 500
					
					.create-time
						color: $text-color-tertiary
						font-size: 22rpx
				
				.toolbar
					display: flex
					align-items: center
					color: $text-color-tertiary
					
					icon
						transition: all 0.2s ease
						padding: 10rpx // 增大点击区域
					
					.likeNum
						font-size: 26rpx
						width: 60rpx // 固定宽度防止点赞数变化时布局跳动
						text-align: left
						margin: 0 10rpx 0 4rpx
			
			// 评论内容
			.comment
				text-align: justify
				font-size: 30rpx // 略微增大，提升可读性
				color: $text-color-base
				line-height: 1.7
				
			// 查看更多回复（按钮样式）
			.comment-more
				font-size: 26rpx
				color: $primary-color
				padding: 8rpx 0
				align-self: flex-start
			
			// 子评论区域（全新样式）
			.childComments
				margin-top: 10rpx
				padding: 20rpx
				padding-left: 25rpx // 左侧留出空间给线条
				border-left: 4rpx solid $border-color // 使用线条表示层级
				display: flex
				flex-direction: column
				gap: 15rpx // 子评论间距
				
				.child-item
					font-size: 26rpx
					color: $text-color-secondary
					line-height: 1.6
					
					.childItem-time
						color: $text-color-tertiary
						font-size: 22rpx
						margin-left: 15rpx

/* --- 评论输入栏 --- */
.commnet-input-bar
	position: fixed
	bottom: 0
	left: 0
	width: 100%
	background: $bg-color
	padding: 15rpx 20rpx
	box-sizing: border-box
	border-top: 1rpx solid #dcdcdc
	display: flex
	align-items: center
	gap: 20rpx
	z-index: 100

	.comment-input
		flex: 1
		height: 70rpx
		background: $bg-color-alt
		border-radius: 35rpx
		padding: 0 30rpx
		font-size: 28rpx
		
	.commentInputP
		color: $text-color-tertiary
	
	.cancle
		font-size: 30rpx
		color: $text-color-secondary
		padding: 0 10rpx

</style>