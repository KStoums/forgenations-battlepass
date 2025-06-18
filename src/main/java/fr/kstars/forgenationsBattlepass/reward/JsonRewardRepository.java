package fr.kstars.forgenationsBattlepass.reward;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JsonRewardRepository implements RewardRepository {
    private List<Reward> rewards;

    @Override
    public Optional<Reward> findByNameAndLevel(String rewardName, int rewardLevel) {
        return this.rewards.stream().findFirst().filter(reward -> reward.getName().equals(rewardName) && reward.getLevel() == rewardLevel);
    }

    @Override
    public List<Reward> findAll() {
        return this.rewards;
    }

    @Override
    public List<Reward> findAllByLevel(int rewardLevel) {
        return this.rewards.stream().filter(reward -> reward.getLevel() == rewardLevel).collect(Collectors.toList());
    }

    @Override
    public List<Reward> findAllFree() {
        List<Reward> freeRewards = new ArrayList<>();
        for (Reward reward : this.rewards) {
            if (reward.isPremium()) {
                continue;
            }

            freeRewards.add(reward);
        }

        return freeRewards;
    }

    @Override
    public List<Reward> findAllPremium() {
        List<Reward> premiumRewards = new ArrayList<>();
        for (Reward reward : this.rewards) {
            if (!reward.isPremium()) {
                continue;
            }

            premiumRewards.add(reward);
        }

        return premiumRewards;
    }

    @Override
    public List<Reward> findByPage(int page, boolean premiumRewards) { //TODO CHANGE METHOD EMPLACEMENT
        List<Reward> pageRewards = new ArrayList<>();
        if (premiumRewards) {
            int endIndex = 9 * page;
            int startIndex = (endIndex - 9) + 1; //+1 because the first level is not 0 but 1

            List<Reward> rewards = this.findAllPremium();
            for (Reward reward : rewards) {
                if (!(reward.getLevel() >= startIndex && reward.getLevel() <= endIndex)) {
                    continue;
                }

                pageRewards.add(reward);
            }

            return pageRewards;
        }

        int endIndex = 9 * page;
        int startIndex = (endIndex - 9) + 1; //+1 because the first level is not 0 but 1

        List<Reward> rewards = this.findAllFree();
        for (Reward reward : rewards) {
            if (!(reward.getLevel() >= startIndex && reward.getLevel() <= endIndex)) {
                continue;
            }

            pageRewards.add(reward);
        }

        return pageRewards;
    }

    @Override
    public void updateRewards(Reward[] rewards) {
        this.rewards = Arrays.stream(rewards).toList();
    }
}