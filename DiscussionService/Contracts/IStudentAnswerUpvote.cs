using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IStudentAnswerUpvoteRepository : IRepositoryBase<StudentAnswerUpvote>
    {
        Task<StudentAnswerUpvote> GetStudentAnswerUpvote(StudentAnswerUpvote studentUpvote);
        void CreateStudentAnswerUpvote(StudentAnswerUpvote studentUpvote);
        void UpdateStudentAnswerUpvote(StudentAnswerUpvote studentUpvote);
        void DeleteStudentAnswerUpvote(StudentAnswerUpvote studentUpvote);
    }
}