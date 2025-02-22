import request from '@/utils/request'

// 查询系统会议列表
export function listMeeting(query) {
  return request({
    url: '/system/meeting/list',
    method: 'get',
    params: query
  })
}

// 查询系统会议详细
export function getMeeting(meetingId) {
  return request({
    url: '/system/meeting/' + meetingId,
    method: 'get'
  })
}

// 新增系统会议
export function addMeeting(data) {
  return request({
    url: '/system/meeting',
    method: 'post',
    data: data
  })
}

// 修改系统会议
export function updateMeeting(data) {
  return request({
    url: '/system/meeting',
    method: 'put',
    data: data
  })
}

// 删除系统会议
export function delMeeting(meetingId) {
  return request({
    url: '/system/meeting/' + meetingId,
    method: 'delete'
  })
}
