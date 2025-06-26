<template>
	<view class="rank-container">
	    <view class="tab">
	    	<view :class="'tab-item '+ ((currentIndex == tabIndex)? 'active': '')" 
			v-for="(tabItem,tabIndex) in rankData"  :key="engName" :data-index="tabIndex" 
			@click="showRank">
	    		<text>{{tabIndex == 0? '全部': tabItem.standard}}</text>
	    		<view v-if="currentIndex == tabIndex" class="tabLine"></view>
	    	</view>
	    </view>
	    <scroll-view class="tab-content" scroll-y="true" :scroll-top="scrollTop.scrollTop_value" @scroll="scrollFun">
	    	<view class="content-item" v-for="(contentItem,contentIndex) in rankData"  :key="contentItem.engName" :data-type="contentItem.engName" :hidden="currentIndex != contentIndex">
	    		<view class="book-item" v-for="(bookItem,bookIndex) in (contentItem.qdRank || contentItem.zhRank)"   :key="bookId" :data-bookId="bookItem.id" @click="gotoBookDetail">
	                <view class="bookInfo">
	                    <text class="bookName">{{bookItem.factionName}}\n</text>
	                    <text class="author">{{bookItem.author}}\n</text>
	                    <text class="bookDes">{{bookItem.des}}</text>
	                    <icon :hidden="bookIndex == 0" class="first-icon"></icon>
	                </view>
	                <image :src="bookItem.headerImage" mode="scaleToFill" />
	    		</view>
	    		
	    	</view>
	    </scroll-view>
	    <view class="backTop" @click="backToTop" :hidden="!scrollTop.backTop_show"><icon class="backTop-icon"></icon></view>
	</view>
</template>

<script>
	var Api = require('../../utils/api/api');
	var Util = require('../../utils/util');
	export default {
		data() {
			return {
					rankData: [],
				    currentIndex: 0,
				    scrollTop: {scrollTop_value: 0, backTop_show: false}
			};
		},
		onLoad: function(options) {
		        var self = this;
		        //show loading
		        uni.showToast({
		          title: '加载中',
		          icon: 'loading',
		          duration: 10000
		        });
		        //options rankType
		        uni.request({
				  url: Api.getRank(options.rankType),
				  header: {'content-type': 'application/json'},
				  success: function(res) {
		            //hide loading
		            setTimeout(function(){
		                uni.hideToast();
		            },500);
		            if(res.data.data){
						self.rankData = res.data.data
						console.log("排行榜数据:",res.data.data)
		            }else{
		                console.log('请求排行榜数据失败....')
		            }
				  },
		          error: function(err){
		            console.log('请求排行榜数据失败....'+err);
		          }
		        });
		    },
			methods:{
				 showRank: function(event){
					 this.currentIndex = event.target.index
				    },
				    scrollFun: function(event){
				        if(event.detail.scrollTop > 300){//触发backtop的显示条件  
						  this.scrollTop.backTop_show = true;
				        }else{
						  this.scrollTop.backTop_show=false
				        }
				    },
				    backToTop: function(){
				        var topValue = this.scrollTop.scrollTop_value;//发现设置scroll-top值不能和上一次的值一样，否则无效，所以这里加了个判断  
				        if(topValue == 1){  
				          topValue = 0;  
				        }else{  
				          topValue = 1;  
				        }
						this.scrollTop.scrollTop_value=topValue
				    },
				    gotoBookDetail: function(event){
				        var bookId = event.target.bookid;
				        uni.navigateTo({
				          url: '../book_detail/book_detail?bookId='+bookId
				        });
				    }
			}
	}
</script>

<style lang="stylus">
@charset "UTF-8";
.rank-container {
  position: relative;
  overflow: hidden;
  height: 100%; }

.rank-container .tab {
  overflow-x: auto;
  white-space: nowrap;
  height: 6.4%;
  top: 0;
  left: 0; }

/*可滑动但是不显示滚动条*/
.rank-container .tab::-webkit-scrollbar {
  width: 0;
  height: 0;
  color: transparent; }

.rank-container .tab .tab-item {
  line-height: 80rpx;
  height: 80rpx;
  display: inline-block;
  margin-right: 20rpx;
  width: 120rpx;
  text-align: center;
  font-size: 28rpx; }

.rank-container .tab .tab-item.active {
  font-size: 32rpx;
  color: #fd9941; }

.rank-container .tab .tab-item .tabLine {
  height: 4rpx;
  width: 100%;
  background-color: #fd9941;
  z-index: 1000;
  margin-top: -6rpx; }

.rank-container .tab-content {
  height: 93.6%; }

.rank-container .content-item {
  padding-top: 30rpx; }

.rank-container .content-item:first-child {
  padding-top: 10rpx; }

.rank-container .content-item .book-item {
  /*高度塌陷*/
  overflow: hidden;
  padding: 0 15rpx; }

.rank-container .content-item .book-item > image {
  height: 180rpx;
  width: 140rpx;
  box-shadow: 4rpx 4rpx 2rpx #888;
  float: left;
  display: inline-block;
  margin-left: -100%;
  margin-bottom: 20rpx; }

.rank-container .content-item .book-item .bookInfo {
  float: left;
  width: 100%;
  padding: 10rpx 10rpx 10rpx 180rpx;
  font-size: 22rpx;
  box-sizing: border-box;
  position: relative;
  margin-bottom: 20rpx; }

.rank-container .content-item .book-item .bookInfo::after {
  content: "";
  display: none;
  clear: both; }

.rank-container .content-item .book-item > text {
  display: block;
  color: #4d5154; }

.rank-container .bookInfo .bookName {
  font-size: 28rpx;
  font-weight: bold;
  color: #4c4d4f; }

.rank-container .bookInfo .author {
  font-size: 24rpx;
  margin-top: 10rpx;
  color: #737a80;
  line-height: 46rpx; }

.rank-container .bookInfo .bookDes {
  font-size: 24rpx;
  color: #5a5f62;
  margin-top: 20rpx; }

.rank-container .bookInfo .text-underline {
  border-bottom: 4rpx solid #fd9941;
  margin-right: 10rpx;
  color: #888; }

.rank-container .bookInfo > icon {
  position: absolute;
  right: 20rpx;
  top: 10rpx;
  display: inline-block;
  width: 30rpx;
  height: 45rpx; }

.rank-container .backTop {
  position: fixed;
  bottom: 70rpx;
  right: 50rpx;
  height: 80rpx;
  width: 80rpx;
  background-color: #d7d7d7;
  z-index: 1000;
  text-align: center; }

.rank-container .backTop .backTop-icon {
  background-size: 300rpx 300rpx;
  background-position: -244rpx -245rpx;
  display: inline-block;
  width: 60rpx;
  height: 60rpx;
  margin-top: 10rpx; }

</style>
