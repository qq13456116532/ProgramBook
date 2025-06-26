<template>
	<view class="shop-container">
  <view class="search">
    <view class="section">
      <view class="form">
        <view>
			<view class="input-block">
<!-- <input @focus="setIsSearching" @blur="judgeIsNull" @input="finishedInput" :value="searchValue" placeholder="请输入搜索内容"> -->
		<input placeholder="请输入搜索内容" @focus="setIsSearching" @blur="judgeIsNull" 
		@input="finishedInput"
			v-model="searchValue" />
		</view>
		
        <view class="search-block" :style="'display:' + (isSearching? 'none': 'inline-block')">
          <icon class="search-icon" color="#fff"></icon>
          <!-- <text>请输入搜索内容</text> -->
        </view>
      </view>
    </view>
  </view>
  <view :class="'search-content '+ isSearching? 'pt-page-moveToLeft': ''" :style="'display:' + isSearching?'block': 'none'">
    
    <view class="books" v-if="searchData.length > 0">
	<template v-for="item in searchData" >
		<navigator :key="item.id" :url="'/pages/book_detail/book_detail?bookid='+item.id+'&name='+item.factionName">
		  <view class="book-item">
		    <image  class="book-img fade-in" :src="item.headerImage"></image>
		    <view class="book-info">
		      <text class="book-name">{{item.factionName}}</text>
		      <text class="book-des">简介：{{item.des}}</text>
		      <view class="book-author">作者：{{item.author}}</view>
		    </view>
		  </view>
		</navigator>
	</template>
    </view>
    <view class="no-data" v-else-if="isSearching">暂无搜索到相关书籍</view>
	
  </view>
  <view class="shop-content">
    <view class="" topic>
      <view class="header">
        <text class="text-underline">推荐</text>专题
      </view>
      <view class="topic-container">
        <view class="topic-item" v-for="item in topic" :key="item.id">
          <image :src="item.headImg" mode="scaleToFill" @click="goRecommendDetail"></image>
        </view>
      </view>
    </view>
    <view class="sort">
      <view class="header">
        <text class="text-underline">分类</text>
      </view>
      <view class="sort-container">
        <navigator url="/pages/classify/classify?index=0" class="sort-item" data-engName="total">
          <icon class="iconfont  myall-icon"></icon>
          <view>全部</view>
        </navigator>
        <navigator url="/pages/classify/classify?index=1" class="sort-item" data-engName="xuanhuan">
          <icon class="iconfont  myxuanhuan-icon"></icon>
          <view>C语言</view>
        </navigator>
        <navigator url="/pages/classify/classify?index=2" class="sort-item" data-engName="yanqing">
          <icon class="iconfont myyanqing-icon"></icon>
          <view>JAVA</view>
        </navigator>
        <navigator url="/pages/classify/classify?index=3" class="sort-item" data-engName="wuxia">
          <icon class="iconfont mywuxia-icon"></icon>
          <view>Python</view>
        </navigator>
        <navigator url="/pages/classify/classify?index=4" class="sort-item" data-engName="lishi">
          <icon class="iconfont mylishi-icon"></icon>
          <view>前端</view>
        </navigator>
        <navigator url="/pages/classify/classify?index=5" class="sort-item" data-engName="kehuan">
          <icon class="iconfont mykehuan-icon"></icon>
          <view>其他</view>
        </navigator>
      </view>
    </view>
    <view class="recommend">
      <view class="header">
        <text class="text-underline">热门</text>推荐
        <view class="recommend-container">
          <view class="recommend-item" v-for="item in recommend"
		  :key="item.id" :data-bookid="item.id" :style="((item.index+1)%4 == 0)? 'margin-right:0': 'margin-right:12%'" @click="gotoBookDetail">
            <image :src="item.headerImage" mode="scaleToFill"></image>
            <view class="bookName">{{item.factionName}}</view>
            <view class="author">{{item.author}}</view>
          </view>
        </view>
      </view>
    </view>
   <!-- <view class="rank">
      <view class="qdRank" data-rankType="qd" @click="gotoRank">起点小说榜</view>
      <view class="zhRank" data-rankType="zh" @click="gotoRank">纵横小说榜</view>
    </view>
    <view class="new">
      <view class="header">
        <text class="text-underline">新书</text>推荐
      </view>
      <view class="new-container">
        <view class="new-item" v-for="item in recommend" :key="item.id" :data-bookid="item.id" :style="((item.index+1)%4 == 0)? 'margin-right:0': 'margin-right:12%'"  @click="gotoBookDetail">
          <image :src="item.headerImage" mode="scaleToFill"></image>
          <view class="bookName">{{item.factionName}}</view>
          <view class="author">{{item.author}}</view>
        </view>
      </view> -->
    </view>
  </view>
</view>
</view>
</template>

<script>
	var Api = require('../../utils/api/api');
	var Util = require('../../utils/util');
	//获取应用实例
	var app = getApp();
	export default {
		data() {
			return {
				  topic: [
				      {
				        headImg: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/CHATGPT.jpg',
				        id: '01'
				      },
				      {
				        headImg: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/DEEPLEARNING.jpg',
				        id: '02'
				      },
				      {
				        headImg: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/VR.jpg',
				        id: '03'
				      }
				    ],
				    recommend: [],
				    new: [],
				    isSearching: false,
				    searchValue: '',
				    searchData: [],
					token: ""
			};
		},
		computed: {
			getIsSearhing(){
				return "display:" + this.isSearching? 'none': 'inline-block';
			},
			//search-content {{isSearching? 'pt-page-moveToLeft': ''}} 16
			getSearchContent(){
				return "search-content "+ this.isSearching? 'pt-page-moveToLeft': '';
			},
			//display: {{isSearching? 'block': 'none'}} 16
			getDisplay(){
				return this.isSearching?'block': 'none';
			},
		},
		created() {
			this.token = wx.getStorageSync('token');
			this.getData();
		},
		onHide(){
			this.isSearching = false;
			this.searchValue=""
			this.searchData=[]
		},
		methods:{
			  finishedInput: function (event) {
			    var self = this;
				console.log("完成输入", event.detail.value)
				
			    var searchStr = event.detail.value;
			    if (searchStr) {
			      uni.request({
			        url: Api.searchBook(searchStr),
			        header: { 'content-type': 'application/json' },
			        success: function (res) {
			        console.log('Debug: res', res);
			          const data = res.data.data;
			          //隐藏加载信息  
			          setTimeout(function () {
			            uni.hideToast();
			          }, 300);
			          if (data) {
						self.searchData=data
			          } else {
			            uni.showToast({ title: '搜索书籍失败' + res.data.msg ? '，' + res.data.msg : '', icon: 'none' });
			          }
			        },
			        error: function (err) {
			          setTimeout(function () {
			            uni.hideToast();
			            uni.showToast({ title: '搜索书籍失败~', icon: 'none' });
			          }, 500);
			        }
			      });
			    } else {
			      console.log("但是输入内容是空的")					
				  self.isSearching=false;
				  self.searchData=[]
			    }
			  },
			  judgeIsNull: function (event) {
			    if (event.detail.value == '') {
			      this.isSearching=false;
			    } else {
			      this.isSearching=true;
			    }
			  },
			setIsSearching: function () {
			      this.isSearching=true;
			},
			clearSearchContent: function () {
			    this.searchValue="";
			  },
			    // 跳转到详情页
			    goRecommendDetail: function (event) {},
			    gotoBookDetail: function (event) {
					// console.log("gotoBookDetail,event是",event)
			      var bookid = event.currentTarget.dataset.bookid;
			      // 判断当前书籍在不在我的书单中
			      uni.navigateTo({
			        url: '../book_detail/book_detail?bookid=' + bookid
			      });
			    },
				  gotoRank: function (event) {
					  console.log("正在执行gotoRank，event是",event)
				    var rankType = event.currentTarget.dataset.ranktype;
				    uni.navigateTo({
				      url: '../rank/rank?rankType=' + rankType
				    });
				  },
				    getData() {
				      var self = this;
					  console.log("当前执行getData")
				      uni.request({
				        url: Api.getRecommendBooks(),
				        method: 'GET',
				        success(res) {
				          if (!res.data.data || res.data.data.length === 0) {
				            console.log("getData的结果",res)
				            // uni.request({
				            //   url: Api.initDataBase(),
				            //   method: 'GET',
				            //   success(res) {
				            //     console.log(res)
				            //     self.getData();
				            //   },
				            //   fail(e) {
				            //     uni.showToast({ title: '获取书籍失败', icon: 'none' });
				            //   }
				            // });
				            return;
				          }
						  self.recommend=res.data.data
						  self.new = res.data.data
				
				        },
				        fail(e) {
				          uni.showToast({ title: '获取书籍失败', icon: 'none' });
				        }
				      });
				    }
		}
	}
</script>
<style lang="stylus">
@charset "UTF-8";
.shop-container {
  display: -webkit-flex;
  /* Safari */
  display: flex;
  flex-direction: column;
  align-items: stretch;
  height: 100%;
  // overflow: hidden;
}

.shop-container .search {
  background-color: #fd9941;
  flex: 0 1 85rpx;
}

.shop-container .search .section {
  width: 96%;
  margin: 0 auto;
  height: 70rpx;
  position: relative;
}

.shop-container .section .form {
  background-color: #fbb16f;
  width: 100%;
  height: 100%;
}

.shop-container .search .input-block {
  width: 100%;
  height: 100%;
  line-height: 100%;
  color: #fff;
  display: inline-block;
  border-radius: 6rpx;
  font-size: 28rpx;
  position: relative;
}

.shop-container .search .input-block > input {
  width: 100%;
  padding-left: 20rpx;
  height: 100%;
  box-sizing: border-box;
}

.shop-container .search .input-block > icon {
  height: 70%;
  position: absolute;
  right: 17rpx;
  top: 17rpx;
  z-index: 999;
  font-size: 30rpx;
}

.shop-container .search .search-block {
  position: absolute;
  top: 14rpx;
  left: 20rpx;
  height: 50rpx;
  line-height: 100%;
  font-size: 28rpx;
  color: #fff;
}

.shop-container .search .search-block > text {
  margin-left: 4rpx;
}

.shop-container icon.search-icon {
  width: 50rpx;
  display: inline-block;
  height: 100%;
  background-size: 230rpx 230rpx;
  background-position: -90rpx -90rpx;
  vertical-align: middle;
}

.shop-container .search-content {
  z-index: 1000;
  height: 93.3%;//93.3%
}

.pt-page-moveToLeft {
  -webkit-animation: moveToLeft 0.6s ease both;
  animation: moveToLeft 0.6s ease both;
}

@-webkit-keyframes moveToLeft {
  from {
    -webkit-transform: translateX(-100%);
  }
  to {
    -webkit-transform: translateX(0%);
  }
}

@keyframes moveToLeft {
  form {
    transform: translateX(-100%);
  }
  to {
    transform: translateX(0%);
  }
}

/*可滑动但是不显示滚动条*/
::-webkit-scrollbar {
  width: 0;
  height: 0;
  color: transparent;
}

/*书城主体内容*/
.shop-content {
  background-color: #fff;
  flex: 1;
  overflow: auto;
  padding: 15rpx 15rpx 0 15rpx;
}

.shop-content .header {
  padding: 6rpx 0 15rpx 0;
  font-size: 30rpx;
  font-weight: bold;
}

.topic-container {
  overflow-x: auto;
  white-space: nowrap;
}

.topic-item {
  display: inline-block;
  width: 48.6%;
  text-align: center;
  margin-right: 20rpx;
}

.topic-item:last-child {
  margin-right: 0;
}

.topic-item image {
  border-radius: 6rpx;
  width: 100%;
  height: 192rpx;
}

.rank {
  padding: 20rpx 0;
  white-space: nowrap;
  overflow: hidden;
  text-align: center;
  color: #fff;
  font-size: 32rpx;
}

.rank .qdRank {
  display: inline-block;
  width: 30%;
  height: 90rpx;
  margin-right: 40rpx;
  border-radius: 6rpx;
  background-color: #fe7156;
  line-height: 90rpx;
}

.rank .zhRank {
  display: inline-block;
  width: 30%;
  height: 90rpx;
  background-color: #30d590;
  border-radius: 6rpx;
  line-height: 90rpx;
}

/*热门推荐*/
.recommend-container {
	  margin-left: 11%; /* 设置左侧外边距为10像素 */

  margin-top: 12rpx;
  display: flex;
  display: -webkit-flex;
  flex-flow: row wrap;
  justify-content: flex-start;
  align-items: flex-start;
  align-content: flex-start;
}

.recommend-container .recommend-item {
  flex: 0 0 21%;
  max-width: 21%;
  height: 200rpx;
  margin-right: 5.3%;
  margin-bottom: 100rpx;
}

.recommend-container .recommend-item image {
  width: 100%;
  height: 100%;
}

.recommend-container .recommend-item .bookName {
  font-size: 24rpx;
  color: #000;
  font-weight: bold;
}

.recommend-container .recommend-item .author {
  font-size: 20rpx;
  color: #666;
}

/*分类*/
.sort-container {
  display: flex;
  display: -webkit-flex;
  flex-flow: row nowrap;
  justify-content: space-between;
  font-size: 24rpx;
  padding: 20rpx 40rpx 30rpx 40rpx;
  text-align: center;
}

.sort-item {
  width: 80rpx;
}

.sort-item > icon {
  display: inline-block;
  width: 60rpx;
  height: 60rpx;
  background-size: 300rpx 300rpx;
}
.myall-icon {
  // background-position: -58rpx -120rpx;
}
.myall-icon::before{
	content: '\e635';
}
.all-icon {
  background-position: -58rpx -120rpx;
}
.myxuanhuan-icon::before{
	content: '\e61a';
	// background-position: -119rpx -120rpx;
	
}
.xuanhuan-icon {
  background-position: -119rpx -120rpx;
}
.myyanqing-icon::before{
	content: '\e639';
}
.yanqing-icon {
  background-position: -182rpx -120rpx;
}
.mywuxia-icon::before{
	content: '\e664';
}
.wuxia-icon {
  background-position: -244rpx -120rpx;
}
.mylishi-icon::before{
	content: '\e69a';
}
.lishi-icon {
  background-position: 5rpx -182rpx;
}
.mykehuan-icon::before{
	content: '\e618';
	
}
.kehuan-icon {
  background-position: -57rpx -182rpx;
}

/*新书*/
.new-container {
  display: flex;
  display: -webkit-flex;
  flex-flow: row wrap;
  justify-content: flex-start;
  align-items: flex-start;
  align-content: flex-start;
}

.new-container .new-item {
  flex: 0 0 21%;
  max-width: 21%;
  height: 200rpx;
  margin-right: 5.3%;
  margin-bottom: 100rpx;
}

.new-container .new-item image {
  width: 100%;
  height: 100%;
}

.new-container .new-item .bookName {
  font-size: 24rpx;
  color: #000;
  font-weight: bold;
}

.new-container .new-item .author {
  font-size: 20rpx;
  color: #666;
}

/*搜索*/
.search-tips {
  padding: 20rpx;
}

.search-tips .tips-item {
  display: inline-block;
  padding: 4rpx 24rpx;
  height: 40rpx;
  border-radius: 20rpx;
  border: 3rpx #d4d6d8 solid;
  color: #49505a;
  font-size: 24rpx;
  line-height: 40rpx;
}

.history {
  border-bottom: 1px solid #d7d8da;
}

.history-item {
  padding: 0 20rpx;
  font-size: 30rpx;
  font-weight: bold;
  line-height: 100%;
  position: relative;
}

.history-item > icon {
  vertical-align: middle;
  top: 23rpx;
  position: absolute;
}

.history-item view {
  padding: 20rpx 0;
  margin-left: 43rpx;
  border-bottom: 1px solid #d7d8da;
}

.history-item:last-child view {
  border-bottom: none;
}

.clearBtn {
  height: 40rpx;
  text-align: center;
  margin-top: 40rpx;
}

.clearBtn > text {
  background: transparent;
  font-size: 24rpx;
  line-height: 40rpx;
  color: #1b88ee;
  border: none;
  width: 100%;
}

.book-item {
  display: -webkit-flex;
  display: flex;
  flex-flow: row nowrap;
  justify-content: space-between;
  align-items: stretch;
  padding: 0 32rpx 30rpx 32rpx;
  box-sizing: border-box;
  border-bottom: 1px solid #d9d9d9;
}

.book-item:last-child {
  border-bottom: none;
}

.book-item .book-img {
  width: 180rpx;//180rpx
  height: 240rpx;
  background-color: #dddddd;
}

.book-item .book-info {
  flex: 1;
  padding-left: 20rpx;
  color: #333;
}

.book-item .book-name {
  display: block;
  font-size: 32rpx;
}

.book-item .book-des {
  font-size: 24rpx;
  margin-top: 20rpx;
  text-align: justify;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 4;
  overflow: hidden;
}

.book-item .book-author {
  display: block;
  font-size: 26rpx;
  color: #999;
  margin-top: -4rpx;
}

.book-item .book-author icon {
  font-size: 26rpx;
  margin-right: 20rpx;
}

.books {
  padding-top: 20rpx;
}

.no-data {
  text-align: center;
  font-size: 26rpx;
  color: #ddd;
  padding: 60rpx 0;
}

</style>
