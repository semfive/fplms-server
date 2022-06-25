using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IStudentUpvoteRepository : IRepositoryBase<StudentUpvote>
    {
        Task<StudentUpvote> GetStudentUpvote(StudentUpvote studentUpvote);
        void CreateStudentUpvote(StudentUpvote studentUpvote);
        void UpdateStudentUpvote(StudentUpvote studentUpvote);
        void DeleteStudentUpvote(StudentUpvote studentUpvote);
    }
}