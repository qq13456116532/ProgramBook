<template>
	<!-- The template from the previous refactoring is perfect and does not need to change. -->
	<!-- It is compatible with both Vue 2 and Vue 3. -->
	<view class="booklist-container">
		<!-- Search bar section -->
		<view class="search">
			<view class="section">
				<view class="form">
					<view class="input-block">
						<input 
							placeholder="请输入搜索内容" 
							v-model="searchValue"
							@focus="isSearching = true" 
							@blur="isSearching = !!searchValue" 
						/>
					</view>
					<view class="search-block" v-show="!isSearching">
						<icon class="icon-small" type="search" color="#fff"></icon>
					</view>
				</view>
				<view class="toShop" @tap="goToShop">
					<icon class="shop-icon"></icon>
				</view>
			</view>
		</view>

		<!-- Header section for time/month selection -->
		<view class="header">
			<view class="headerLine"></view>
			<text class="headerTime">{{ headerText }}</text>
			<view class="timeTab">
				<view 
					v-for="item in showMonths" 
					:key="item.month"
					:class="['everyTime', { 'active': item.monthIndex === selectedMonthIndex }]"
					@click="selectedMonthIndex = item.monthIndex"
				>
					<text>{{ item.monthCname }}</text>
					<icon :class="{ 'hasRecord': item.hasRecord }"></icon>
				</view>
			</view>
		</view>

		<!-- List section -->
		<view class="list">
			<view class="all" v-if="filteredBooks.length > 0">
				<!-- NEW -->
				<view class="book-item" v-for="book in filteredBooks" :key="book.id" @click="goToBookDetail(book.id)">
				    <image class="book-item__image" :src="book.headerImage" mode="aspectFill" />
				    <view class="book-item__info">
				        <text class="book-item__title">{{ book.factionName }}</text>
				        <text class="book-item__author">
				            <text class="text-underline">作者：</text>{{ book.author }}
				        </text>
				        <text class="book-item__description">简介： {{ book.des }}</text>
				        <icon :class="['read-icon', book.hasRead === 0 ? 'isRead' : 'notRead']"></icon>
				    </view>
				</view>
			</view>

			<view class="empty" v-else-if="books.length === 0">
				<image src="https://bpic.588ku.com/element_origin_min_pic/00/77/63/0056e18ce712923.jpg"></image>
				<button @click="goToShop" plain="true" type="default">您还未添加书籍，快去书城看看</button>
			</view>
            
			<view class="empty" v-else>
				<image src="https://bpic.588ku.com/element_origin_min_pic/00/77/63/0056e18ce712923.jpg"></image>
				<text>没有找到相关的书籍</text>
			</view>
		</view>

		<!-- Authorization modal -->
		<template v-if="!isAuthorized">
			<view class="mask"></view>
			<view class='box'>
				<view class='msg-box'>
					<text class='title'>授权登录</text>
					<text class='aumsg'>We微书申请获得以下权限</text>
					<text class='msg'>●设置你的公开信息（昵称、头像等）</text>
					<view class="headimg">
						<button 
							class="avatar-button"
							open-type="chooseAvatar" 
							@chooseavatar="handleChooseAvatar"
						>
							<image :src="authForm.avatar" model="center" />
						</button>
					</view>
					<input 
						v-model="authForm.nickname" 
						type="nickname" 
						class="weui-input"
						placeholder="请输入昵称" 
					/>
					<view class='btn_wrapper'>
						<button @click='handleAuthCancel' class='btn'>拒绝</button>
						<button class='btn confirm_btn' @click='handleAuthorize'>允许</button>
					</view>
				</view>
			</view>
		</template>
	</view>
</template>

<script>
// Use the standard Options API for Vue 2
import { getMyBooks, GetOnLogin, GetjudgeExpire } from '../../utils/api/api';
import { eNumToCNum } from '../../utils/util';

export default {
	data() {
		return {
			// All state properties go here
			authForm: {
				nickname: '',
				avatar: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/132.jfif',
				avatarFile: null, // To store the base64 or temp file path
			},
			isAuthorized: true, // Controls the authorization modal
			books: [], // Raw list of books from the API
			searchValue: '',
			isSearching: false,
			headerText: '',
			showMonths: [],
			selectedMonthIndex: 0,
		};
	},

	computed: {
		// Computed properties are a feature of both Vue 2 and 3
		filteredBooks() {
			if (!this.searchValue) {
				return this.books; // Return all books if search is empty
			}
			const searchTerm = this.searchValue.toLowerCase();
			return this.books.filter(book => 
				book.factionName.toLowerCase().includes(searchTerm) ||
				book.author.toLowerCase().includes(searchTerm)
			);
		}
	},

	// uni-app lifecycle hooks
	onReady() {
		const timeResult = this.generateMonths();
		this.showMonths = timeResult.showMonths;
		this.headerText = timeResult.headerText;
	},

	async onShow() {
		// Using async/await is perfectly fine in Vue 2 methods/hooks
		await this.checkAuthAndFetchData();
		this.searchValue = ''; // Reset search on show
	},

	methods: {
		// All functions go into the `methods` object
		async checkAuthAndFetchData() {
			try {
				const token = uni.getStorageSync('token');
				if (!token) {
					console.log("No token found, showing auth modal.");
					this.showAuthModal();
					return;
				}

				const [err, res] = await uni.request({ url: GetjudgeExpire(token) });
				if (err || res.data.code !== 200) {
					console.log("Token expired or invalid, showing auth modal.");
					this.showAuthModal();
					return;
				}

				console.log("Token is valid.");
				this.isAuthorized = true;
				uni.showTabBar({ animation: true });
				await this.fetchMyBooks(token);

			} catch (error) {
				console.error("Authorization check failed:", error);
				this.showAuthModal();
			}
		},

		async fetchMyBooks(token) {
			uni.showLoading({ title: '获取书单中...' });
			try {
				const [err, res] = await uni.request({ url: getMyBooks(token) });
				if (res && res.data && res.data.data) {
					this.books = res.data.data;
					console.log('Successfully fetched books:', this.books.length);
				} else {
					this.books = [];
				}
			} catch (error) {
				console.error("Failed to fetch books:", error);
				uni.showToast({ title: '获取书单失败', icon: 'error' });
			} finally {
				uni.hideLoading();
			}
		},

		showAuthModal() {
			this.isAuthorized = false;
			uni.hideTabBar({ animation: true });
		},

		handleAuthCancel() {
			this.isAuthorized = true;
			uni.showTabBar({ animation: true });
		},

		async handleAuthorize() {
			if (!this.authForm.nickname) {
				return uni.showToast({ title: '请输入昵称', icon: 'none' });
			}
			if (!this.authForm.avatarFile) {
				return uni.showToast({ title: '请选择头像', icon: 'none' });
			}
			
			uni.showLoading({ title: '登录中...' });

			try {
				const [loginErr, loginRes] = await uni.login();
				if (loginErr) throw new Error('微信登录失败');
				
				const [reqErr, res] = await uni.request({
					url: GetOnLogin(),
					method: 'POST',
					data: {
						code: loginRes.code,
						nickName: this.authForm.nickname,
						avatar: this.authForm.avatarFile,
					}
				});
				
				const loginData = res.data;
				if (reqErr || loginData.code !== 200 || !loginData.data.token) {
					throw new Error(loginData.msg || '服务器登录失败');
				}
				
				uni.setStorageSync('token', loginData.data.token);
				await this.checkAuthAndFetchData();
				uni.showToast({ title: '登录成功', icon: 'success' });

			} catch (error) {
				console.error("Authorization process failed:", error);
				uni.showToast({ title: error.message || '登录时发生错误', icon: 'error' });
			} finally {
				uni.hideLoading();
			}
		},

		handleChooseAvatar(e) {
			const { avatarUrl } = e.detail;
			this.authForm.avatar = avatarUrl;
			
			uni.getFileSystemManager().readFile({
				filePath: avatarUrl,
				encoding: 'base64',
				success: res => {
					this.authForm.avatarFile = 'data:image/jpeg;base64,' + res.data;
				},
				fail: err => {
					console.error("Failed to read avatar file:", err);
					uni.showToast({ title: '头像处理失败', icon: 'none' });
				}
			});
		},

		generateMonths() {
			const resultArray = [];
			const today = new Date();
			const currentMonth = today.getMonth();
			
			for (let i = 0; i <= 6; i++) {
				const month = currentMonth - i;
				const adjustedMonth = month < 0 ? month + 12 : month;
				resultArray.push({
					monthCname: `${eNumToCNum(adjustedMonth + 1)}月`,
					hasRecord: i === 0,
					monthIndex: i
				});
			}
			return {
				headerText: `${today.getFullYear()}年${resultArray[0].monthCname}`,
				showMonths: resultArray.reverse()
			};
		},

		goToShop() {
			uni.switchTab({ url: '../shop/shop' });
		},

		goToBookDetail(bookId) {
			uni.navigateTo({ url: `../book_detail/book_detail?bookid=${bookId}` });
		}
	}
}
</script>
<style>
/* --- 1. Design System: CSS Variables --- */
.booklist-container {
	/* Colors */
	--color-primary: #fd9941;
	--color-primary-light: #fbb16f;
	--color-surface: #ffffff;
	--color-background: #f7f7f8;
	--color-text-primary: #333333;
	--color-text-secondary: #888888;
	--color-text-on-primary: #ffffff;
	--color-border: #eeeeee;
	--color-success: #07c160;
	/* Spacing */
	--spacing-small: 8rpx;
	--spacing-medium: 16rpx;
	--spacing-large: 24rpx;
	--spacing-xlarge: 32rpx;
	/* Borders */
	--border-radius-small: 8rpx;
	--border-radius-medium: 12rpx;
	--border-radius-large: 20rpx;
	/* Shadows */
	--shadow-soft: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	--shadow-medium: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

/* --- 2. Global & Layout --- */
.booklist-container {
	background-color: var(--color-background);
	min-height: 100vh;
	display: flex;
	flex-direction: column;
	position: relative;
	height: 100%;
	top: -1rpx;
}

/* --- 3. Search Bar --- */
.search-bar, .booklist-container .search {
	background-color: var(--color-primary);
	padding: var(--spacing-medium) var(--spacing-xlarge);
	flex-shrink: 0;
	flex: 0 1 85rpx;
}
.search-bar .section, .booklist-container .search .section {
	display: flex;
	align-items: center;
	gap: var(--spacing-large);
	width: 96%;
	margin: 0 auto;
	height: 70rpx;
	position: relative;
}
.search-bar .form, .booklist-container .section .form {
	flex-grow: 1;
	position: relative;
	background-color: var(--color-primary-light);
	border-radius: var(--border-radius-large);
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
}
.search-bar .input-block, .booklist-container .search .input-block {
	width: 100%;
	height: 100%;
	line-height: 100%;
	color: #fff;
	display: inline-block;
	border-radius: 6rpx;
	font-size: 28rpx;
	position: relative;
}
.search-bar .input-block>input, .booklist-container .search .input-block>input {
	width: 100%;
	padding-left: 20rpx;
	height: 100%;
	box-sizing: border-box;
	color: var(--color-text-on-primary);
	font-size: 28rpx;
}
.search-bar .search-block, .booklist-container .search .search-block {
	position: absolute;
	top: 14rpx;
	right: 20rpx;
	height: 50rpx;
	line-height: 100%;
	font-size: 28rpx;
	color: #fff;
	display: flex;
	align-items: center;
	gap: var(--spacing-small);
	pointer-events: none;
}
.booklist-container .search .search-block>text { margin-left: 4rpx; }
.booklist-container icon.search-icon,
.search-bar icon.search-icon {
	width: 50rpx;
	display: inline-block;
	height: 100%;
	background-size: 230rpx 230rpx;
	background-position: -90rpx -90rpx;
	vertical-align: middle;
}
.booklist-container .toShop {
	width: 80rpx;
	height: 100%;
	position: absolute;
	right: 0;
	top: 0;
}
.booklist-container icon.shop-icon,
.toShop .shop-icon {
	display: inline-block;
	width: 40rpx;
	height: 40rpx;
	background-size: 230rpx 230rpx;
	background-position: 0rpx -100rpx;
	position: absolute;
	right: 10rpx;
	top: 15rpx;
	filter: brightness(0) invert(1);
}

/* --- 4. Timeline/Header Tabs --- */
.timeline-header, .booklist-container .header {
	text-align: center;
	position: relative;
	background-color: var(--color-surface);
	padding: var(--spacing-large) 0;
	margin-bottom: var(--spacing-medium);
	box-shadow: var(--shadow-soft);
}
.timeline-header .headerLine, .booklist-container .header .headerLine {
	display: inline-block;
	height: 2rpx;
	width: 80%;
	background-color: var(--color-border);
	margin-top: 40rpx;
}
.timeline-header .headerTime, .booklist-container .header .headerTime {
	background-color: var(--color-surface);
	display: block;
	height: 20rpx;
	line-height: 20rpx;
	width: 240rpx;
	font-size: 32rpx;
	font-weight: bold;
	position: absolute;
	left: 50%;
	top: 52rpx;
	transform: translateX(-50%);
	color: var(--color-text-secondary);
}
.timeline-tabs, .booklist-container .header .timeTab {
	margin-top: var(--spacing-large);
	display: flex;
	justify-content: center;
	gap: var(--spacing-medium);
	padding-bottom: 20rpx;
}
.timeline-tab, .booklist-container .header .timeTab .everyTime {
	position: relative;
	display: inline-block;
	height: 100rpx;
	width: 100rpx;
	text-align: center;
	border-radius: var(--border-radius-medium);
	transition: all 0.2s ease-in-out;
}
.timeline-tab text, .booklist-container .header .timeTab .everyTime>text {
	font-size: 24rpx;
	color: var(--color-text-secondary);
	vertical-align: middle;
	margin-right: 10rpx;
	line-height: 90rpx;
}
.timeline-tab--active, .booklist-container .header .timeTab .everyTime.active {
	border: none;
	background-color: var(--color-primary);
	border-radius: 10rpx;
	box-shadow: 2rpx 2rpx 2rpx #cccccc;
}
.timeline-tab--active text, .booklist-container .header .timeTab .everyTime.active>text {
	color: var(--color-text-on-primary);
	font-weight: bold;
}
.timeline-tab > icon, .booklist-container .header .timeTab .everyTime>icon {
	position: absolute;
	left: 50%;
	top: 80%;
	transform: translateX(-50%);
	display: block;
	height: 10rpx;
	width: 10rpx;
	border: none;
	border-radius: 5rpx;
	background-color: transparent;
}
.timeline-tab > icon.hasRecord,
.booklist-container .header .timeTab .everyTime>icon.hasRecord {
	background-color: var(--color-text-secondary);
}
.timeline-tab--active > icon.hasRecord,
.booklist-container .header .timeTab .everyTime.active>icon.hasRecord {
	background-color: var(--color-text-on-primary);
}

/* --- 5. Book List --- */
.book-list, .booklist-container .list {
	padding: 0 var(--spacing-xlarge);
	flex-grow: 1;
	margin-top: 20rpx;
	padding: 30rpx 30rpx;
	background-color: #fff;
}
.booklist-container .list .empty,
/* .empty {
	text-align: center;
	padding-top: 100rpx;
	color: var(--color-text-secondary);
} */
.empty {
    display: flex;
    flex-direction: column; /* 垂直排列 */
    align-items: center;    /* 水平居中 */
    justify-content: flex-start; /* 上对齐 */
    padding-top: 100rpx;
    color: var(--color-text-secondary);
    min-height: 400rpx;     /* 可选，根据你页面高度调整 */
}
.empty > image {
    margin-bottom: 32rpx;   /* 图片和下面文字拉开距离 */
}
.empty > text, 
.empty > button {
    margin-top: 16rpx;
}

.booklist-container .list .empty>image, .empty > image {
	margin-top: 40rpx;
	height: 300rpx;
	width: 300rpx;
	opacity: 0.7;
}
.booklist-container .list .empty>button, .empty > button {
	margin-top: var(--spacing-xlarge);
	width: 70%;
	height: 70rpx;
	line-height: 70rpx;
	border-color: var(--color-primary);
	color: var(--color-primary);
	font-size: 30rpx;
	background-color: transparent;
	border-radius: var(--border-radius-large);
	border: 2rpx solid var(--color-primary);
}

/* --- 6. Book Item --- */
.book-item, .book-item-wrapper {
	display: flex;
	gap: var(--spacing-large);
	background-color: var(--color-surface);
	padding: var(--spacing-large);
	margin-bottom: var(--spacing-large);
	border-radius: var(--border-radius-medium);
	box-shadow: var(--shadow-soft);
	transition: box-shadow 0.2s ease, transform 0.2s ease;
	position: relative;
	min-height: 200rpx;
}
.book-item-wrapper > image, .book-item__image {
	width: 140rpx;
	height: 180rpx;
	box-shadow: 4rpx 4rpx 2rpx #888;
	border-radius: var(--border-radius-small);
	flex-shrink: 0;
	background-color: var(--color-background);
	position: absolute;
	left: 0;
	top: 10rpx;
}
.book-item__info, .booklist-container .list .bookInfo {
	flex-grow: 1;
	display: flex;
	flex-direction: column;
	position: relative;
	min-width: 0;
	float: left;
	width: 100%;
	padding: 10rpx 10rpx 10rpx 180rpx;
	font-size: 22rpx;
	box-sizing: border-box;
	margin-bottom: 20rpx;
	height: 200rpx;
	overflow: hidden;
}
.booklist-container .list .bookInfo::after {
	content: '';
	display: none;
	clear: both;
}
.book-item__title, .booklist-container .bookInfo .bookName {
	font-size: 24rpx;
	font-weight: bold;
	color: var(--color-text-primary);
	margin-bottom: var(--spacing-small);
}
.book-item__author, .booklist-container .bookInfo .author {
	font-size: 22rpx;
	color: var(--color-text-secondary);
	margin-top: 10rpx;
	margin-bottom: var(--spacing-medium);
}
.book-item__description, .booklist-container .bookInfo .bookDes {
	font-size: 22rpx;
	color: var(--color-text-secondary);
	line-height: 1.5;
	margin-top: 20rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
}
.book-item__info .text-underline, .booklist-container .bookInfo .text-underline {
	border-bottom: 4rpx solid var(--color-primary-light);
	margin-right: 10rpx;
	color: #888;
	padding-bottom: 2rpx;
}
.book-item__info > .read-icon,
.booklist-container .bookInfo>icon {
	position: absolute;
	right: 20rpx;
	top: 10rpx;
	display: inline-block;
	width: 30rpx;
	height: 45rpx;
}
.read-icon.isRead, .booklist-container .bookInfo>icon.isRead {
	background-size: 210rpx 210rpx;
	background-position: -133rpx -80rpx;
}
.read-icon.notRead, .booklist-container .bookInfo>icon.notRead {
	background-size: 210rpx 210rpx;
	background-position: -176rpx -80rpx;
}

/* --- 7. Avatar Button --- */
.avatar-button {
	width: 100rpx; 
	height: 100rpx; 
	padding: 0; 
	border: none;
	border-radius: 50rpx;
	overflow: hidden;
}
.avatar-button image {
	width: 100%;
	height: 100%;
}

/* --- 8. WeUI input --- */
.weui-input, .auth-modal .weui-input {
	text-align: center;
	margin: 20rpx 0;
	width: 80%;
	padding: var(--spacing-medium);
	background-color: var(--color-background);
	border-radius: var(--border-radius-small);
	margin-bottom: var(--spacing-large);
}

/* --- 9. Modal/Mask/Box --- */
.mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.6);
	z-index: 100;
	height: 100vh;
	width: 100vw;
}
.box {
	width: 100%;
	height: 50%;
	left: 12%;
	top: 20%;
	position: fixed;
	z-index: 101;
}
.msg-box, .auth-modal__content {
	display: flex;
	flex-direction: column;
	align-items: center;
	background-color: var(--color-surface);
	border-radius: 5px;
	width: 76%;
	padding-top: 30rpx;
	border: 6rpx solid black;
	padding: var(--spacing-xlarge);
	box-shadow: var(--shadow-medium);
}
.title, .auth-modal .title {
	margin: 10rpx;
	width: 100%;
	text-align: center;
	padding-bottom: 10px;
	border-bottom: 2rpx solid rgba(7, 17, 27, 0.1);
	font-size: 36rpx;
	font-weight: bold;
	color: var(--color-text-primary);
	padding-bottom: var(--spacing-medium);
	margin-bottom: var(--spacing-large);
}
.msg, .auth-modal .msg {
	font-size: 12px;
	color: #ccc;
	border-top: 2rpx solid rgba(7, 17, 27, 0.1);
	padding-top: 20rpx;
	width: 80%;
	font-size: 24rpx;
	color: var(--color-text-secondary);
	background-color: var(--color-background);
	padding: var(--spacing-medium);
	border-radius: var(--border-radius-small);
	width: 100%;
	margin: var(--spacing-large) 0;
}
.btn_wrapper, .auth-modal .btn_wrapper {
	display: flex;
	flex-direction: row;
	align-items: center;
	height: 100rpx;
	line-height: 100rpx;
	border-top: 4rpx solid rgba(7, 17, 27, 0.1);
	width: 100%;
	border-top: 1rpx solid var(--color-border);
	margin-top: var(--spacing-large);
}
.btn, .auth-modal .btn {
	background-color: #fff;
	flex: 1;
	height: 100rpx;
	line-height: 100rpx;
	text-align: center;
	font-size: 36rpx;
	width: 200%;
	border-radius: 0;
	background-color: transparent;
	font-size: 32rpx;
	border-radius: 0;
	color: var(--color-text-secondary);
	padding: 0;
}
.aumsg, .auth-modal .aumsg {
	padding-top: 80rpx;
	font-size: 34rpx;
	width: 80%;
	text-align: center;
	color: var(--color-text-primary);
}
.confirm_btn, .auth-modal .confirm_btn {
	border-left: 2rpx solid rgba(7, 17, 27, 0.1);
	color: var(--color-success);
	font-weight: bold;
	border-left: 1rpx solid var(--color-border);
}
.btn::after, .auth-modal .btn::after {
	border: 0;
}

/* --- 10. Head image in modal --- */
.box .msg-box .headimg, .auth-modal .headimg {
	background-color: #ebe9fd;
	text-align: center;
	color: #ffffff;
	margin: var(--spacing-large) 0;
}
.box .msg-box .headimg image, .auth-modal .headimg image {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50rpx;
}
</style>
