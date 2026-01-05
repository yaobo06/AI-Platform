import Cookies from 'js-cookie'

const ForumTokenKey = 'Forum-Token'

export function getForumToken() {
  return Cookies.get(ForumTokenKey)
}

export function setForumToken(token) {
  return Cookies.set(ForumTokenKey, token, { expires: 30 })
}

export function removeForumToken() {
  return Cookies.remove(ForumTokenKey)
}
