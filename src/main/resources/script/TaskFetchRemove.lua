-- KEYS[1] = zset key
-- ARGV[1] = maxScore
-- ARGV[2] = limit

local zkey = KEYS[1]
local maxScore = ARGV[1]
local limit = tonumber(ARGV[2])

-- 1. 批次取出 score <= maxScore 的元素
local tasks = redis.call('ZRANGEBYSCORE', zkey, '-inf', maxScore, 'LIMIT', 0, limit)

-- 2. 如果沒有元素直接回傳
if #tasks == 0 then
    return {}
end

-- 3. 一次 ZREM 刪除（比逐筆刪除快很多）
redis.call('ZREM', zkey, unpack(tasks))

-- 4. 回傳被刪除的項目
return tasks