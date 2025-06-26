import Promise from './es6-promise.min';

const loading = (title) => {
  title = title ? title : '玩命加载中...'
  wx.showLoading({
    title: title,
  })
}

//判断用户填写的是username还是email
function judgeTelOrName(str){
  var phoneRegExp = new RegExp('^1[3|4|5|8][0-9]\d{4,8}$', 'g');
  var usernameRegExp = new RegExp('^(?![^a-zA-Z_]+$)(?!\D+$).{6,18}$', 'g');
  if(usernameRegExp.test(str)){
    return 'using_name';
  }else if(phoneRegExp.test(str)){
    return 'using_email';
  }else{
    return 'error';
  }
}
function eNumToCNum(num){
  switch(num){
    case 1: return '一';
    case 2: return '二';
    case 3: return '三';
    case 4: return '四';
    case 5: return '五';
    case 6: return '六';
    case 7: return '七';
    case 8: return '八';
    case 9: return '九';
    case 10: return '十';
    case 11: return '十一';
    case 12: return '十二';
    default:
      console.log("传入的参数不是1~12的数字。");
      return '';
  }
}

//格式化时间函数01
function formatTime(date) {
  let year = date.getFullYear()
  let month = date.getMonth() + 1
  let day = date.getDate()

  let hour = date.getHours()
  let minute = date.getMinutes()
  let second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}
//格式化时间函数02
function formatTime2(time) {
  if (typeof time !== 'number' || time < 0) {
    return time
  }

  var hour = parseInt(time / 3600)
  time = time % 3600
  var minute = parseInt(time / 60)
  time = time % 60
  var second = time

  return ([hour, minute, second]).map(function (n) {
    n = n.toString()
    return n[1] ? n : '0' + n
  }).join(':')
}

/**
 * format date to 'a minute ago, an hour ago, one day ago'
 * date nowtime
 */
function formatDate3(date) {
  var resultStr = '';
  var timePre = date.getTime();
  var now = new Date();
  var timeNow = now.getTime();
  if(timeNow >= timePre){
    var distance = (timeNow - timePre)/1000;
    if(distance >= 0 && distance < 60){
      resultStr = '刚刚';
    }else if(distance >= 60 && distance <= (60*60)){
      resultStr = Math.floor(distance/60)+'分钟前';
    }else if(distance > 3600 && distance <= (24*60*60)){
      resultStr = Math.floor(distance/3600)+"小时前";
    }else if(distance > 86400 && distance/(30*24*60*60)){
      resultStr = Math.floor(distance/86400)+"天前"
    }else{
      resultStr = formatTime(date);
    }
  }else{
    console.log('nowTime is behind on this time');
  }
  return resultStr;
}

function formatNumber(n) {
  n = n.toString()
  return n[1] ? n : '0' + n
}

function getDateStr(date) {
  if (!date) return '';
  return date.getFullYear() + '年' +  (date.getMonth() + 1) + '月' +date.getDate() + '日';
}

/**
 * 生成GUID序列号
 * @returns {string} GUID
 */
function guid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

/**
 * 记录日志
 * @param {Mixed} 记录的信息
 * @returns {Void}
 */
function log(msg) {
  if (!msg) return;
  if (getApp().settings['debug']){}
    // console.log(msg);
  let logs = wx.getStorageSync('logs') || [];
  // logs.unshift(msg)
  wx.setStorageSync('logs', logs)
}

/**
 * @param {Function} func 接口
 * @param {Object} options 接口参数
 * @returns {Promise} Promise对象
*/
function promiseHandle(func, options) {
  options = options || {};
  return new Promise((resolve, reject) => {
    if (typeof func !== 'function')
        reject();
    options.success = resolve;
    options.fail = reject;
    func(options);
  });
}

/**
 * 显示错误函数
 * @param obj 操作对象
 * @param errorMsg 需要显示的错误信息
 */
function showErrMsg(obj, errorMsg, time){
  obj.err_tips_data={err_tips_show: true, err_tips_text: errorMsg}
  uni.showToast({
  	icon: "error",
	title: errorMsg
  })
  setTimeout(function(){
	  obj.err_tips_data={err_tips_show: false, err_tips_text: ''}
  }, time || 3000);
}
const menuToTree = (menu) => {
  // 来自这篇博客，谢谢: https://blog.csdn.net/u013373006/article/details/82108873
  menu.forEach(function(item) {
    delete item.children;
  });
  var map = {};
  menu.forEach(function(item) {
    map[item.id] = item;
  });
  var val = [];
  menu.forEach(function(item) {
    var parent = map[item.pid];
    if (parent) {
      (parent.children || (parent.children = [])).push(item);
    } else {
      val.push(item);
    }
  });
  return val;
}
const menuSortIds = (menuTree) => {
  let docs = []
  for (let i in menuTree) {
    docs.push(menuTree[i].id)
    if (menuTree[i].children) {
      docs = docs.concat(menuSortIds(menuTree[i].children))
    }
  }
  return docs
}
const menuTreeReaded = (menuTree, docId) => {
  for (let i in menuTree) {
    if (menuTree[i].id == docId) {
      menuTree[i].readed = true
      break
    }
    if (menuTree[i].children) {
      menuTree[i].children = menuTreeReaded(menuTree[i].children, docId)
    }
  }
  return menuTree
}
const setReaderSetting = (obj) => {
  wx.setStorageSync("reader-setting", JSON.stringify(obj))
}

const getReaderSetting = () => {
  let val = wx.getStorageSync("reader-setting")
  if (!val) {
    return {
      themeIndex: 0,
      fontIndex: 0
    }
  }
  val = JSON.parse(val)
  if (val.themeIndex == undefined) val.themeIndex = 0
  if (val.fontIndex == undefined) val.fontIndex = 0
  return val
}
//导出以上函数
module.exports = {
  formatTime: formatTime,
  formatTime2: formatTime2,
  formatDate3: formatDate3,
  judgeTelOrName: judgeTelOrName,
  eNumToCNum: eNumToCNum,
  guid: guid,
  log: log,
  promiseHandle: promiseHandle,
  getDateStr: getDateStr,
  formatNumber: formatNumber,
  showErrMsg: showErrMsg
}