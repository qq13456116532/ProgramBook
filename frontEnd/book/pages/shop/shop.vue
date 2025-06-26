<template>
	<view class="shop-container">
		<!-- 搜索区域 -->
		<view class="search">
			<view class="section">
				<view class="form">
					<view class="input-block">
						<input 
							placeholder="请输入搜索内容" 
							v-model.trim="searchValue"
							@focus="handleSearchFocus"
							@blur="handleSearchBlur"
							@input="handleSearchInput"
						/>
					</view>
					<!-- 使用v-show，当input聚焦时，此占位符隐藏，性能优于v-if -->
					<view class="search-block" v-show="!isSearching">
						<icon class="search-icon" color="#fff"></icon>
					</view>
				</view>
			</view>
		</view>

		<!-- 搜索结果页 (使用v-if，不搜索时不渲染DOM) -->
		<view 
			class="search-content" 
			:class="{ 'pt-page-moveToLeft': isSearching }"
			v-if="isSearching"
		>
			<view class="books" v-if="searchData.length > 0">
				<!-- v-for可以直接放在navigator上，key是必须的 -->
				<navigator 
					v-for="item in searchData" 
					:key="item.id" 
					:url="`/pages/book_detail/book_detail?bookid=${item.id}&name=${item.factionName}`">
					<view class="book-item">
						<image class="book-img fade-in" :src="item.headerImage"></image>
						<view class="book-info">
							<text class="book-name">{{item.factionName}}</text>
							<text class="book-des">简介：{{item.des}}</text>
							<view class="book-author">作者：{{item.author}}</view>
						</view>
					</view>
				</navigator>
			</view>
			<!-- 当进入搜索状态但没有数据时显示提示 -->
			<view class="no-data" v-else>暂无搜索到相关书籍</view>
		</view>
		
		<!-- 主内容区域 (使用v-show，避免搜索时主内容区域销毁和重建) -->
		<view class="shop-content" v-show="!isSearching">
			<!-- 推荐专题 -->
			<view class="topic">
				<view class="header">
					<text class="text-underline">推荐</text>专题
				</view>
				<view class="topic-container">
					<view class="topic-item" v-for="item in topic" :key="item.id">
						<image :src="item.headImg" mode="scaleToFill" @click="goRecommendDetail(item)"></image>
					</view>
				</view>
			</view>

			<!-- 分类导航 (数据驱动) -->
			<view class="sort">
				<view class="header">
					<text class="text-underline">分类</text>
				</view>
				<view class="sort-container">
					<navigator v-for="(item, index) in categories" :key="item.engName" :url="`/pages/classify/classify?index=${index}`" class="sort-item">
						<icon class="iconfont" :class="item.iconClass"></icon>
						<view>{{item.name}}</view>
					</navigator>
				</view>
			</view>

			<!-- 热门推荐 -->
			<view class="recommend">
				<view class="header">
					<text class="text-underline">热门</text>推荐
				</view>
				<view class="recommend-container">
					<!-- 移除了内联样式，改用CSS nth-child -->
					<view 
						class="recommend-item" 
						v-for="item in recommend" 
						:key="item.id" 
						@click="gotoBookDetail(item.id)">
						<image :src="item.headerImage" mode="scaleToFill"></image>
						<view class="bookName">{{item.factionName}}</view>
						<view class="author">{{item.author}}</view>
					</view>
				</view>
			</view>
			
			<!-- 
				原有的rank和new部分被注释，保持原样。
				如果需要，可以仿照“热门推荐”的结构进行重构。
			-->
			<!-- <view class="rank">...</view> -->
			<!-- <view class="new">...</view> -->
		</view>
	</view>
</template>

<script>
	// 使用 import 语法
	import Api from '../../utils/api/api';
	// import Util from '../../utils/util'; // 如果 Util 文件未使用，可以移除

	// getApp() 可以在需要时直接调用，无需在顶部定义
	// const app = getApp();

	export default {
		data() {
			return {
				topic: [
					{ id: '01', headImg: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/CHATGPT.jpg' },
					{ id: '02', headImg: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/DEEPLEARNING.jpg' },
					{ id: '03', headImg: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/VR.jpg' }
				],
				// 将分类数据化，便于维护
				categories: [
					{ name: '全部', engName: 'total', iconClass: 'myall-icon' },
					{ name: 'C语言', engName: 'xuanhuan', iconClass: 'myxuanhuan-icon' },
					{ name: 'JAVA', engName: 'yanqing', iconClass: 'myyanqing-icon' },
					{ name: 'Python', engName: 'wuxia', iconClass: 'mywuxia-icon' },
					{ name: '前端', engName: 'lishi', iconClass: 'mylishi-icon' },
					{ name: '其他', engName: 'kehuan', iconClass: 'mykehuan-icon' },
				],
				recommend: [],
				newBooks: [], // 建议使用更明确的命名，如 newBooks
				
				// 搜索相关状态
				isSearching: false,
				searchValue: '',
				searchData: [],
				
				// 防抖计时器
				searchTimer: null,
				
				token: ""
			};
		},
		created() {
			this.token = uni.getStorageSync('token');
			this.fetchInitialData();
		},
		// 页面隐藏时重置搜索状态
		onHide() {
			this.resetSearchState();
		},
		methods: {
			// --- 搜索相关方法 ---
			handleSearchFocus() {
				this.isSearching = true;
			},
			
			handleSearchBlur() {
				// 如果输入框为空，则在失焦时退出搜索状态
				if (!this.searchValue) {
					this.isSearching = false;
				}
			},
			
			// 使用防抖处理输入，避免频繁请求
			handleSearchInput() {
				// 清除上一个计时器
				if (this.searchTimer) {
					clearTimeout(this.searchTimer);
				}
				
				// 如果输入为空，立即清空结果并返回
				if (!this.searchValue) {
					this.searchData = [];
					return;
				}

				// 设置一个新的计时器，500ms后执行搜索
				this.searchTimer = setTimeout(() => {
					this.executeSearch(this.searchValue);
				}, 500); // 500ms的延迟
			},

			executeSearch(keyword) {
				uni.showLoading({ title: '搜索中...' });
				uni.request({
					url: Api.searchBook(keyword),
					header: { 'content-type': 'application/json' },
					// 使用箭头函数，this自动指向Vue实例
					success: (res) => {
						if (res.data && res.data.code === 200) { // 假设成功的响应有特定标识
							this.searchData = res.data.data || [];
						} else {
							uni.showToast({ title: '搜索失败' + (res.data.msg ? `：${res.data.msg}` : ''), icon: 'none' });
							this.searchData = [];
						}
					},
					fail: (err) => {
						uni.showToast({ title: '网络错误，搜索失败', icon: 'none' });
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},

			resetSearchState() {
				this.isSearching = false;
				this.searchValue = "";
				this.searchData = [];
				if(this.searchTimer) clearTimeout(this.searchTimer);
			},
			
			// --- 页面导航方法 ---
			goRecommendDetail(item) {
				// 可以根据item.id或其他信息进行跳转
				console.log('跳转到专题详情', item.id);
				// uni.navigateTo({ url: `/pages/topic_detail/topic_detail?id=${item.id}` });
			},
			
			gotoBookDetail(bookId) {
				if (!bookId) return;
				uni.navigateTo({
					url: `/pages/book_detail/book_detail?bookid=${bookId}`
				});
			},
			
			gotoRank(rankType) {
				uni.navigateTo({
					url: `/pages/rank/rank?rankType=${rankType}`
				});
			},
			
			// --- 数据获取方法 ---
			fetchInitialData() {
				uni.request({
					url: Api.getRecommendBooks(),
					method: 'GET',
					success: (res) => {
						// 增加对返回数据的健壮性检查
						if (res.data && res.data.data && res.data.data.length > 0) {
							this.recommend = res.data.data;
							this.newBooks = res.data.data; // 如果新书和推荐是相同数据
						} else {
							console.warn("获取推荐书籍数据为空或格式不正确", res);
						}
					},
					fail: (e) => {
						uni.showToast({ title: '获取推荐书籍失败', icon: 'none' });
					}
				});
			}
		}
	}
</script>
<style lang="stylus">
// --- 变量定义 ---
// 颜色变量
primary-color = #fd9941
secondary-color = #fbb16f
white-color = #fff
dark-text-color = #333
gray-text-color = #666
light-gray-text-color = #999
border-color = #d9d9d9
placeholder-bg-color = #dddddd
success-color = #30d590
danger-color = #fe7156

// 尺寸与间距变量
base-padding = 15rpx
base-border-radius = 6rpx

// --- 基础布局 ---
.shop-container
  display: flex
  flex-direction: column
  align-items: stretch
  height: 100%

// --- 搜索栏 ---
.search
  flex: 0 1 85rpx
  background-color: primary-color

  .section
    position: relative
    width: 96%
    height: 70rpx
    margin: 0 auto

    .form
      position: relative
      width: 100%
      height: 100%
      background-color: secondary-color
      border-radius: base-border-radius

      .input-block
        width: 100%
        height: 100%
        
        input
          width: 100%
          height: 100%
          padding-left: 20rpx
          box-sizing: border-box
          font-size: 28rpx
          color: white-color
      
      .search-block
        position: absolute
        top: 14rpx
        left: 20rpx
        display: flex
        align-items: center
        height: 50rpx
        font-size: 28rpx
        color: white-color
        pointer-events: none // 使其不阻挡 input 的 focus 事件

        .search-icon
          width: 50rpx
          height: 100%
          background-size: 230rpx 230rpx
          background-position: -90rpx -90rpx

// --- 搜索结果页 ---
.search-content
  position: absolute
  top: 85rpx // 搜索栏高度
  left: 0
  right: 0
  bottom: 0
  z-index: 1000
  background-color: white-color
  overflow-y: auto

  .books
    .book-item
      display: flex
      padding: 30rpx 32rpx
      border-bottom: 1px solid border-color
      &:last-child
        border-bottom: none

      .book-img
        width: 180rpx
        height: 240rpx
        flex-shrink: 0
        background-color: placeholder-bg-color

      .book-info
        flex: 1
        padding-left: 20rpx
        color: dark-text-color
        
        .book-name
          display: block
          font-size: 32rpx
          font-weight: bold

        .book-des
          margin-top: 20rpx
          font-size: 24rpx
          color: gray-text-color
          text-align: justify
          // 文本超出4行显示省略号
          display: -webkit-box
          -webkit-box-orient: vertical
          -webkit-line-clamp: 4
          overflow: hidden

        .book-author
          display: block
          margin-top: 20rpx
          font-size: 26rpx
          color: light-gray-text-color

  .no-data
    padding: 60rpx 0
    text-align: center
    font-size: 26rpx
    color: light-gray-text-color

// --- 主内容区 ---
.shop-content
  flex: 1
  overflow: auto
  padding: base-padding
  background-color: white-color

  // 各模块通用头部样式
  .header
    padding: 6rpx 0 15rpx 0
    font-size: 30rpx
    font-weight: bold

  // 推荐专题模块
  .topic
    .topic-container
      overflow-x: auto
      white-space: nowrap
      &::-webkit-scrollbar
        display: none

      .topic-item
        display: inline-block
        width: 48.6%
        margin-right: 20rpx
        &:last-child
          margin-right: 0

        image
          width: 100%
          height: 192rpx
          border-radius: base-border-radius

  // 分类导航模块
  .sort
    .sort-container
      display: flex
      justify-content: space-between
      padding: 20rpx 40rpx 30rpx
      text-align: center
      font-size: 24rpx

      .sort-item
        width: 80rpx
        
        .iconfont
          display: block
          font-size: 60rpx
          line-height: 1.2
          &.myall-icon::before
            content: '\e635'
          &.myxuanhuan-icon::before
            content: '\e61a'
          &.myyanqing-icon::before
            content: '\e639'
          &.mywuxia-icon::before
            content: '\e664'
          &.mylishi-icon::before
            content: '\e69a'
          &.mykehuan-icon::before
            content: '\e618'

  // 热门推荐模块
  .recommend
    .recommend-container
      display: flex
      flex-flow: row wrap
      margin-left: 11% // 遵循原布局的左边距
      margin-top: 12rpx

      .recommend-item
        flex: 0 0 21%
        max-width: 21%
        height: 200rpx
        margin-right: 12%
        margin-bottom: 100rpx // 为下方文字留出空间
        
        // 每行第三个清除右边距
        &:nth-child(3n)
          margin-right: 0

        image
          width: 100%
          height: 100%

        .bookName, .author
          white-space: nowrap
          overflow: hidden
          text-overflow: ellipsis

        .bookName
          font-size: 24rpx
          color: dark-text-color
          font-weight: bold

        .author
          font-size: 20rpx
          color: gray-text-color

  // 排行榜模块 (样式保留)
  .rank
    padding: 20rpx 0
    color: white-color
    font-size: 32rpx
    text-align: center

    .qdRank, .zhRank
      display: inline-block
      width: 30%
      height: 90rpx
      line-height: 90rpx
      border-radius: base-border-radius

    .qdRank
      margin-right: 40rpx
      background-color: danger-color

    .zhRank
      background-color: success-color

// --- 动画与全局辅助 ---
// 页面滑入动画
.pt-page-moveToLeft
  animation: moveToLeft 0.6s ease both

@keyframes moveToLeft
  from
    transform: translateX(100%)
  to
    transform: translateX(0%)

// 全局隐藏滚动条
::-webkit-scrollbar
  width: 0
  height: 0
  color: transparent
</style>